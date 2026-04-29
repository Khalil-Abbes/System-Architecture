# System Programming and Exception Handling

## Project Context
This repository contains my student implementations for a system programming project targeting a custom RISC-V simulator. All tasks were written in RISC-V assembly.

**Provided by the University:**
A Docker-based RISC-V simulator (accessible at `http://localhost:9000`) with a custom assembler supporting RV32I, RV32M, and Zicsr instructions. Skeleton assembly files in the `programs/` folder, test configuration files in `configs/`, user programs for the process scheduling tasks, and the full hardware/CSR infrastructure (mepc, mtvec, mstatus, mcause, mscratch, mie, mip, mtimecmp, mtime, keyboard/display memory-mapped I/O).

**Implemented by Me:**
All exception handling routines, system calls, interrupt handlers, and the process scheduler — written entirely in RISC-V assembly.

## Tech Stack
* RISC-V Assembly (RV32I, RV32M, Zicsr)
* Docker (RISC-V simulator environment)
* Java, Scala, sbt

## My Contributions

* **System Calls & I/O (Task 1):** Implemented exception handling routines for system call number 11 (print single ASCII character to display) and system call number 4 (print null-terminated string to display), using busy-wait polling for display readiness.
* **Memory-Mapped I/O with Polling (Task 2):** Wrote a program that reads characters from the virtual keyboard and outputs them to the display in order using a polling loop, without interrupts.
* **Memory-Mapped I/O with Interrupts (Task 2 Bonus):** Re-implemented the keyboard/display echo using interrupt-driven I/O with a 16-byte ring buffer, allowing the processor to execute user code between interrupts.
* **Round-Robin Process Scheduler (Task 3):** Implemented a periodic process switch between two non-cooperative processes using timer interrupts (every ~300 cycles), including full register save/restore via per-process control blocks.
* **Process Cloning (Task 4):** Implemented system call 220 (`clone`) to fork a running process into a parent and child, and system call 172 (`getpid`) to return the current process ID. Extended the round-robin scheduler to support up to 8 concurrent processes.

*Key files containing my implementations: assembly files in `programs/`.*
