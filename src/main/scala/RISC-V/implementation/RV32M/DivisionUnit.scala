package RISCV.implementation.RV32M

import chisel3._
import chisel3.util._

import RISCV.interfaces.generic.AbstractExecutionUnit
import RISCV.model._

class DivisionUnit extends AbstractExecutionUnit {
    io.misa := "b01__0000__0_00000_00000_00100_00000_00000".U
    io_data <> DontCare
    io.valid := false.B
    io.stall := STALL_REASON.NO_STALL
    io_reg.reg_write_en := false.B
    io_reg.reg_write_data := 0.U
    io_pc.pc_we := true.B
    io_pc.pc_wdata := io_pc.pc + 4.U

    // Extract fields from the instruction
    val opcode = io.instr(6, 0)
    val funct3 = io.instr(14, 12)
    val funct7 = io.instr(31, 25)
    val rs1 = io.instr(19, 15)
    val rs2 = io.instr(24, 20)
    val rd = io.instr(11, 7)

    io_reg.reg_rs1 := rs1
    io_reg.reg_rs2 := rs2
    io_reg.reg_rd := rd

    val OP = "b0110011".U
    val MULDIV = "b0000001".U

    val msbFunct3 = funct3(2) // Extract the MSB of funct3
    io.valid := (opcode === OP) && (funct7 === MULDIV) && (msbFunct3 === 1.U)
    io.stall := STALL_REASON.NO_STALL

    when(io.valid) {
    io_reg.reg_write_en := true.B
    switch(funct3) {
        is("b100".U) { // DIV
            when(io_reg.reg_read_data2.asSInt === 0.S) { // Division by zero
                io_reg.reg_write_data := "xFFFFFFFF".U // All bits set
            }.elsewhen(io_reg.reg_read_data1.asSInt === Int.MinValue.S && io_reg.reg_read_data2.asSInt === (-1).S) { // Overflow
                io_reg.reg_write_data := io_reg.reg_read_data1 // Quotient is the dividend
            }.otherwise {
                io_reg.reg_write_data := (io_reg.reg_read_data1.asSInt / io_reg.reg_read_data2.asSInt).asUInt
            }
        }
        is("b101".U) { // DIVU
            when(io_reg.reg_read_data2 === 0.U) { // Division by zero
                io_reg.reg_write_data := "xFFFFFFFF".U // All bits set                               // added x here 
            }.otherwise {
                io_reg.reg_write_data := io_reg.reg_read_data1 / io_reg.reg_read_data2
            }
        }
        is("b110".U) { // REM
            when(io_reg.reg_read_data2.asSInt === 0.S) { // Division by zero
                io_reg.reg_write_data := io_reg.reg_read_data1 // Remainder is the dividend
            }.elsewhen(io_reg.reg_read_data1.asSInt === Int.MinValue.S && io_reg.reg_read_data2.asSInt === (-1).S) { // Overflow
                io_reg.reg_write_data := 0.U // Remainder is zero in this overflow condition
            }.otherwise {
                io_reg.reg_write_data := (io_reg.reg_read_data1.asSInt % io_reg.reg_read_data2.asSInt).asUInt
            }
        }
        is("b111".U) { // REMU
            when(io_reg.reg_read_data2 === 0.U) { // Division by zero
                io_reg.reg_write_data := io_reg.reg_read_data1 // Remainder is the dividend
            }.otherwise {
                io_reg.reg_write_data := io_reg.reg_read_data1 % io_reg.reg_read_data2
            }
        }
    }
}

}