// Copyright 2020 The Regents of the University of California
// released under BSD 3-Clause License
// author: Kevin Laeufer <laeufer@cs.berkeley.edu>

package dank.formal.backends

import chisel3.RawModule
import chisel3.stage.ChiselGeneratorAnnotation
import firrtl.{AnnotationSeq, EmittedVerilogCircuitAnnotation}
import firrtl.transforms.BlackBoxInlineAnno

import scala.collection.mutable.ArrayBuffer


object Elaboration {
  private val compiler = new chisel3.stage.ChiselStage

  /** generate verification collateral from a circuit and annotations */
  def elaborate[T <: RawModule](gen: => T, annos: AnnotationSeq): Unit = {

    val circuitAnno = Seq(ChiselGeneratorAnnotation(() => gen))
    val cmds = Array("-X", "sverilog", "-X", "experimental-btor2")
    val result = compiler.execute(cmds, circuitAnno ++ annos)


    val files = result.flatMap {
      case a: EmittedVerilogCircuitAnnotation => Some(a.value.name + a.value.outputSuffix)
      case a: BlackBoxInlineAnno => Some(a.name)
      case other => None
    }
    val name = result.collectFirst{ case a: EmittedVerilogCircuitAnnotation => a.value.name }

  }
}

case class ElaboratedCircuit(name: String, sys: TransitionSystem, files: Seq[String])
