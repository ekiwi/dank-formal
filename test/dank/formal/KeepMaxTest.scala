package dank.formal

import org.scalatest.flatspec.AnyFlatSpec
import chisel3._

/* This module is tested because it uses past values. */
class KeepMax(width: Int) extends CoveredFormalModule {
    val io = IO(new Bundle {
        val in = Input(UInt(width.W))
        val out = Output(UInt(width.W))
    })

    val maxReg = RegInit(0.U(width.W))
    io.out := maxReg

    when (io.in > maxReg) {
        maxReg := io.in
    }

    afterReset() {
        assert(io.out >= past(io.out))
    }
}

class KeepMaxTest extends AnyFlatSpec with SymbiYosysTester {
    behavior of "KeepMax"

    /* Run multiple tests as a regression test for #1. */
    for (i <- 1 until 16) {
        it should s"work for i = $i" in {
            cover(new KeepMax(i), 20).throwErrors()
            prove(new KeepMax(i)).throwErrors()
            bmc(new KeepMax(i), 20).throwErrors()
        }
    }
}
