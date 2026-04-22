# System Architecture

**Course:** System Architecture, Saarland University  
**Instructor:** Prof. Dr. Jan Reineke

## Overview
This repository contains my student implementations for the two projects of the System Architecture course. The projects cover hardware design using the Chisel HDL and low-level system programming in RISC-V assembly.

## Projects

### [Project I — Hardware Design with Chisel](./sysarch-project-one/)
Design and extension of arithmetic circuits and a modular RISC-V processor in Chisel.

**Highlights:**
- Sequential division circuit and parallel vector unit
- RISC-V RV32I processor extensions: function calls, load instructions, and the M-extension (multiply/divide)
- Bonus: hardware-integrated multi-cycle division pipeline

---

### [Project II — System Programming and Exception Handling](./sysarch-project-two/)
Exception handling routines, system calls, interrupt-driven I/O, and a process scheduler — all written in RISC-V assembly.

**Highlights:**
- System calls for character and string output via memory-mapped display
- Interrupt-driven keyboard/display I/O with a ring buffer
- Round-robin process scheduler with timer interrupts and per-process control blocks
- Process cloning (`clone` / `getpid`) supporting up to 8 concurrent processes

---

## Tech Stack
* Scala, Chisel (Hardware Description Language)
* RISC-V Assembly (RV32I, RV32M, Zicsr)
* Java, sbt, Docker
* VS Code with Metals language server
