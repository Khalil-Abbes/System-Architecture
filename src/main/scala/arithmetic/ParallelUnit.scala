package arithmetic

import chisel3._
import scala.reflect.ClassTag
import chisel3.util._
import chisel3.experimental.hierarchy.{Definition, Instance, instantiable, public}

abstract class ComputationalUnit(width: Int) extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(width.W))
        val b = Input(UInt(width.W))
        val c = Output(UInt(width.W))
    })
}

class ParallelUnit(vectorSize: Int, arraySize: Int, unitWidth: Int, comp : (Int) => ComputationalUnit) extends Module {
    require(vectorSize % arraySize == 0)

    val io = IO(new Bundle {
        val a = Input(Vec(vectorSize, UInt(unitWidth.W)))
        val b = Input(Vec(vectorSize, UInt(unitWidth.W)))
        val start = Input(Bool())
        val done = Output(Bool())
        val c = Output(Vec(vectorSize, UInt(unitWidth.W)))
    })

    val units = Seq.fill(arraySize)(Module(comp(unitWidth)))
    val numCycles = vectorSize / arraySize
    val counter = RegInit(0.U(log2Ceil(numCycles + 1).W))
        io.c.foreach(_ := 0.U)  // Default initialization of outputs
        val internalBuffer = Reg(Vec(vectorSize, UInt(unitWidth.W)))

    when(io.start) {
        counter := 1.U
       // printf(p"Start signal received, counter initialized to $counter\n")
        io.done := false.B
    }.otherwise {
        when(counter > 0.U && counter < numCycles.U) {
            counter := counter + 1.U
           // printf(p"Counter incremented to $counter\n")
            io.done := false.B
        }.otherwise {
            counter := 0.U
            io.done := true.B
            io.c := internalBuffer // Copy from internal buffer to output
           // printf(p"Counter reset to $counter, computation completed\n")
        }
    }

    // Ensuring index within bounds and initialization of outputs
      val index = counter  * arraySize.U  % vectorSize.U
    for (i <- 0 until arraySize) {
        units(i).io.a := io.a(index + i.U)
        units(i).io.b := io.b(index + i.U)
        internalBuffer(index + i.U) := units(i).io.c
        // printf(p"Cycle index: $index, Unit $i: A=${io.a(index + i.U)}, B=${io.b(index + i.U)}, Output C=${io.c(index + i.U)}\n")
    }



    // printf(p"Current index: $index, Current counter: $counter\n")
    // for(i <- 0 until vectorSize) {
    //         printf(p"Output Cio=${io.c( i.U)}\n")
    //     }
   // io.done := (counter === numCycles.U)
}