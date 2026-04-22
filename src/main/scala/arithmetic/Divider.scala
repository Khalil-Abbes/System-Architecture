package arithmetic

import chisel3._
import chisel3.util._

class Divider(bitWidth: Int) extends Module {
    val io = IO(new Bundle {
        val start = Input(Bool())
        val done = Output(Bool())
        val dividend = Input(UInt(bitWidth.W))
        val divisor = Input(UInt(bitWidth.W))
        val quotient = Output(UInt(bitWidth.W))
        val remainder = Output(UInt(bitWidth.W))
    })

    val remainder = RegInit(0.U(bitWidth.W))
    val quotient = RegInit(VecInit(Seq.fill(bitWidth)(0.U(1.W))))
    val divisor = RegInit(0.U(bitWidth.W))
    val count = RegInit(0.U(log2Ceil(bitWidth + 1).W)) // To keep track of the division steps
    val busy = RegInit(false.B) // Flag to indicate the division is in process
    val doneReg = RegInit(false.B)

    // Initialize outputs
    io.done := doneReg
    doneReg := false.B
    io.quotient := quotient.asUInt
    io.remainder := remainder

    

    when(io.start && !busy) { // edge cases now
        when(io.divisor === 1.U) { // divisor is 1
            for(i <- 0 until bitWidth) {
            quotient(i) := io.dividend(i) // Correctly assign each bit of dividend to quotient
        }
            remainder := 0.U
            doneReg := true.B
            busy := false.B
        }.elsewhen(io.divisor === 0.U) { // divisor is 0
            quotient.foreach(_ := 1.U) // Set all bits to 1 when dividend is zero
            remainder := io.dividend
            doneReg := true.B
            busy := false.B
        }.elsewhen(io.dividend === 0.U) { // dividend is 0
            quotient.foreach(_ := 0.U) // Set all bits to 0 when dividend is zero
            remainder := 0.U
            doneReg := true.B
            busy := false.B
        }.elsewhen(io.dividend === io.divisor && io.dividend =/= 0.U && io.divisor =/= 0.U ) {    // dividend equal to divisor and both nonzero                      
            quotient.foreach(_ := 0.U) // Set all bits to 0
            quotient(0) := 1.U          // Set only the least significant bit to 1
            remainder := 0.U
            doneReg := true.B
            busy := false.B
        }.elsewhen(io.divisor > io.dividend) { // divisor greater than dividend
            quotient.foreach(_ := 0.U)
            remainder := io.dividend
            doneReg := true.B
            busy := false.B
        }
        .otherwise {
            // Start the normal division process
            remainder := 0.U
            //quotient.foreach(_ := 0.U)
            quotient := VecInit(Seq.fill(bitWidth)(0.U(1.W)))
            divisor := io.divisor
            count := 0.U
            busy := true.B
        }
}. elsewhen(busy) {
        // Perform one step of the division algorithm
      
        val shiftedRemainder = remainder << 1  // Shift the remainder left by one bit

        // Compute the index for accessing a bit from the dividend
        val index = (bitWidth.U - 1.U - count)

        // Extract the bit from the dividend at the computed index
        val dividendBit = io.dividend(index)

        // Add the extracted bit to the shifted remainder
        val tempRemainder = shiftedRemainder + dividendBit


        when(tempRemainder >= divisor) {
            remainder := tempRemainder - divisor
            quotient(index) := 1.U
        }.otherwise {
            remainder := tempRemainder
        }
        count := count + 1.U
        when((count + 1.U) === bitWidth.U) {
            // End of division process
            busy := false.B
            doneReg := true.B
        }
    }
}
