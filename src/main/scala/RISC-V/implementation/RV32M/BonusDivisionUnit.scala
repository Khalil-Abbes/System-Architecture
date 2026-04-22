package RISCV.implementation.RV32M
import arithmetic.Divider  // Importing Divider from the arithmetic package
import chisel3._
import chisel3.util._

import RISCV.interfaces.generic.AbstractExecutionUnit
import RISCV.model._

class BonusDivisionUnit extends AbstractExecutionUnit {
    val divider = Module(new Divider(32)) 
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
    val busy = RegInit(false.B)

    io_reg.reg_rs1 := rs1
    io_reg.reg_rs2 := rs2
    io_reg.reg_rd := rd

     // Connect to divider
    divider.io.start := false.B
    divider.io.dividend := io_reg.reg_read_data1
    divider.io.divisor := io_reg.reg_read_data2

    val OP = "b0110011".U
    val MULDIV = "b0000001".U

    val msbFunct3 = funct3(2) // Extract the MSB of funct3
    when((opcode === OP) && (funct7 === MULDIV) && (msbFunct3 === 1.U)) {
        io.valid := true.B
        when(!busy) {
            divider.io.start := true.B
            busy := true.B
            io.stall := STALL_REASON.EXECUTION_UNIT
        }
    }

    when(divider.io.done) {
        // Division completes
        io.stall := STALL_REASON.NO_STALL
        busy := false.B
        switch(funct3) {
            is("b100".U, "b101".U) { // DIV, DIVU
                io_reg.reg_write_data := divider.io.quotient
            }
            is("b110".U, "b111".U) { // REM, REMU
                io_reg.reg_write_data := divider.io.remainder
            }
        }
        io_reg.reg_write_en := true.B
    }

    when(busy && !divider.io.done) {
        // Maintain stall while division is still processing
        io.stall := STALL_REASON.EXECUTION_UNIT
    }
   


    
}