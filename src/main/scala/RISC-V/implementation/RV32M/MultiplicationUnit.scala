package RISCV.implementation.RV32M

import chisel3._
import chisel3.util._

import RISCV.interfaces.generic.AbstractExecutionUnit
import RISCV.model._


class MultiplicationUnit extends AbstractExecutionUnit {
    io.misa := "b01__0000__0_00000_00000_00100_00000_00000".U
    io_data <> DontCare
    io.valid := false.B
    io.stall := STALL_REASON.NO_STALL
    io_reg.reg_write_en := false.B  // Enable writing to the register file
    io_reg.reg_write_data := 0.U    // Data to write to the register
    io_pc.pc_we := true.B    // Control whether to write to the PC
    io_pc.pc_wdata := io_pc.pc + 4.U    // Data to write to the PC

    // Extract fields from the instruction
  val opcode = io.instr(6, 0)
  val funct3 = io.instr(14, 12)
  val funct7 = io.instr(31, 25)
  val rs1 = io.instr(19, 15)
  val rs2 = io.instr(24, 20)
  val rd = io.instr(11, 7)

    io_reg.reg_rs1 := rs1  // Register source 1 ID (extracted from instruction)
    io_reg.reg_rs2 := rs2  // Register source 2 ID (extracted from instruction)
    io_reg.reg_rd := rd  // Register destination ID




  // Define the expected values for operation and function codes
  val OP = "b0110011".U
  val MULDIV = "b0000001".U
 val msbFunct3 = funct3(2) // Extract the MSB of funct3
  // Set valid if the opcode and funct7 match multiplication operations
  io.valid := (opcode === OP) && (funct7 === MULDIV) && (msbFunct3 === 0.U)
  io.stall := STALL_REASON.NO_STALL

 when (io.valid) {
io_reg.reg_write_en := true.B 
  switch(funct3) {
    is("b000".U) { // MUL
      io_reg.reg_write_data := (io_reg.reg_read_data1.asSInt * io_reg.reg_read_data2.asSInt).asUInt
    }
    is("b001".U) { // MULH
      val product = (io_reg.reg_read_data1.asSInt * io_reg.reg_read_data2.asSInt)
      io_reg.reg_write_data := product(63, 32).asUInt // Extract upper bits for signed*signed
      //printf(s"first num: %d\n", io_reg.reg_read_data1)
      //printf(s"second num: %d\n", io_reg.reg_read_data2)
      //printf(s"result product: %d\n", product)
      //printf(s"result: %d\n", io_reg.reg_write_data)
    }
    is("b010".U) { // MULHSU
      val product = (io_reg.reg_read_data1.asSInt * io_reg.reg_read_data2.asUInt).asSInt
      io_reg.reg_write_data := product(63, 32).asUInt // Extract upper bits for signed*unsigned
    }
    is("b011".U) { // MULHU
      val product = (io_reg.reg_read_data1.asUInt * io_reg.reg_read_data2.asUInt)
      io_reg.reg_write_data := product(63, 32) // Extract upper bits for unsigned*unsigned
    }
  }

}
  }

