package dank.formal

import org.scalatest.flatspec.AnyFlatSpec
import chisel3._

class SmoketestModule extends Module {
    val io = IO(new Bundle {
        val in = Input(Bool())
        val a = Output(Bool())
        val b = Output(Bool())
    })

    val aReg = RegInit(true.B)
    val bReg = RegInit(false.B)
    io.a := aReg
    io.b := bReg

    when (io.in) {
        aReg := true.B
        bReg := false.B
    }.otherwise {
        aReg := false.B
        bReg := true.B
    }
}

class SmoketestSpec(dut: SmoketestModule) extends Spec(dut) {
    cover(dut.aReg)
    cover(dut.bReg)
    assert(dut.aReg ^ dut.bReg)
}

class Smoketest extends AnyFlatSpec with SymbiYosysTester {
    behavior of "SmoketestModule"

    val annos = List(VerificationAspect[SmoketestModule](new SmoketestSpec(_)))

    it should "not smoke" in {
        cover(new SmoketestModule, 20, annos)
        prove(new SmoketestModule, annos)
        bmc(new SmoketestModule, 20, annos)
    }
}