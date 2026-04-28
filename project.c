/* CDA3103 | MIPS CPU Project 

Group Members:
Andrew Reyes
Ryan Demaria
*/

#include "spimcore.h"


/* ALU */
/* 10 Points */
void ALU(unsigned A, unsigned B, char ALUControl, unsigned *ALUresult, char *Zero)
{
    if(ALUControl == 0) { // 000 -> Z = A + B
        *ALUresult = A + B;
    } else if(ALUControl == 1) { // 001 -> Z = A - B
        *ALUresult = A - B;
    } else if(ALUControl == 2) { // 010 -> Z = A < B (signed)

        int signedA = (int)A;
        int signedB = (int)B;

        if (signedA < signedB) { 
            *ALUresult = 1;
        } else {
            *ALUresult = 0;
        }
    } else if(ALUControl == 3) { // 011 -> Z = A < B (unsigned)
        if(A < B) {
            *ALUresult = 1;
        } else {
            *ALUresult = 0;
        }
    } else if(ALUControl == 4) { // 100 -> Z = A AND B
        *ALUresult = A & B;
    } else if(ALUControl == 5) { // 101 Z = A OR B
        *ALUresult = A | B;
    } else if(ALUControl == 6) { // 110 Z = B left shift 16
        *ALUresult = B<<16;
    } else if(ALUControl == 7) { // 111 Z = NOT A
        *ALUresult = ~A;
    }

    if(*ALUresult == 0) {
        *Zero = 1;
    } else {
        *Zero = 0;
    }
}


/* instruction fetch */
/* 10 Points */
int instruction_fetch(unsigned PC, unsigned *Mem, unsigned *instruction)
{
    if(PC % 4 != 0 || PC>>2 >= 16384) { // check word-alignment and address boundaries
        return 1;
    }
        
    *instruction = Mem[PC>>2];
    return 0;
}


/* instruction partition */
/* 10 Points */
void instruction_partition(unsigned instruction, unsigned *op, unsigned *r1,unsigned *r2, unsigned *r3, unsigned *funct, unsigned *offset, unsigned *jsec)
{
    *op = ((instruction>>26) & 63);
    *r1 = ((instruction>>21) & 31);
    *r2 = ((instruction>>16) & 31);
    *r3 = ((instruction>>11) & 31);
    *funct = (instruction & 63);

    *offset = (instruction & 65535);
    *jsec = (instruction & 67108863);
}


/* instruction decode */
/* 15 Points */
int instruction_decode(unsigned op, struct_controls *controls)
{
    if(op == 0) { // r-format (add, sub, and, or, slt, sltu)
        controls->ALUOp = 7; // r-type instruction
        controls->ALUSrc = 0; 
        controls->RegDst = 1; // choose rd as destination
        controls->MemtoReg = 0;
        controls->RegWrite = 1; // write to register
        controls->MemRead = 0; 
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 35) { // lw -> 100011
        controls->ALUOp = 0;
        controls->ALUSrc = 1; // choose immediate value
        controls->RegDst = 0;
        controls->MemtoReg = 1; // write from memory to register
        controls->RegWrite = 1; // write to register
        controls->MemRead = 1; // read from memory
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 15) { // lui -> 001111
        controls->ALUOp = 6;  // shift 16 bits left
        controls->ALUSrc = 1; // choose immediate value
        controls->RegDst = 0;
        controls->MemtoReg = 0;
        controls->RegWrite = 1; // write to register
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 43) { // sw -> 101011
        controls->ALUOp = 0;
        controls->ALUSrc = 1; // choose immediate
        controls->RegDst = 2;
        controls->MemtoReg = 2;
        controls->RegWrite = 0;
        controls->MemRead = 0;
        controls->MemWrite = 1; // write to memory
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 4) { // beq -> 000100
        controls->ALUOp = 1; // subtract to check equality
        controls->ALUSrc = 0;
        controls->RegDst = 2;
        controls->MemtoReg = 2;
        controls->RegWrite = 0;
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 1; // do branch
        controls->Jump = 0;

    } else if(op == 2) { // j -> 000010
        controls->ALUOp = 0;
        controls->ALUSrc = 2;
        controls->RegDst = 2;
        controls->MemtoReg = 2;
        controls->RegWrite = 0;
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 1; // do jump

    } else if(op == 8) { // addi -> 001000
        controls->ALUOp = 0;
        controls->ALUSrc = 1; // choose immediate value
        controls->RegDst = 0;
        controls->MemtoReg = 0;
        controls->RegWrite = 1; // write to register
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 10) { // slti -> 001010
        controls->ALUOp = 2; // set less then
        controls->ALUSrc = 1; // choose immediate value
        controls->RegDst = 0; 
        controls->MemtoReg = 0;
        controls->RegWrite = 1; // write to register
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else if(op == 11) { // sltiu -> 001011
        controls->ALUOp = 3; // set less then unsigned
        controls->ALUSrc = 1; // choose immediate value
        controls->RegDst = 0; 
        controls->MemtoReg = 0;
        controls->RegWrite = 1; // write to register
        controls->MemRead = 0;
        controls->MemWrite = 0;
        controls->Branch = 0;
        controls->Jump = 0;

    } else { // illegal instruction
        return 1;
    }

    return 0;
}


/* Read Register */
/* 5 Points */
void read_register(unsigned r1,unsigned r2,unsigned *Reg,unsigned *data1,unsigned *data2) {
    *data1 = Reg[r1];
    *data2 = Reg[r2];
}


/* Sign Extend */
/* 10 Points */
void sign_extend(unsigned offset,unsigned *extended_value)
{
    int is_negative = ((offset>>15) % 2) == 1;
    if(is_negative) {
        *extended_value = offset | (65535<<16);
    } else {
        *extended_value = offset & (65535);
    }
}


/* ALU operations */
/* 10 Points */
int ALU_operations(unsigned data1,unsigned data2,unsigned extended_value,unsigned funct,char ALUOp,char ALUSrc,unsigned *ALUresult,char *Zero)
{
    if(ALUOp == 7) { // is an R-type instruction

        if(funct == 32) { // add -> 100000
            ALUOp = 0;  // (using ALUOp as a holder for ALUControl)
        } else if(funct == 34) { // sub -> 100010
            ALUOp = 1;
        } else if(funct == 42) { // slt "<" -> 101010
            ALUOp = 2;
        } else if(funct == 43) { // sltu "<" -> 101011
            ALUOp = 3;
        } else if(funct == 36) { // and -> 100100
            ALUOp = 4;
        } else if(funct == 37) { // or -> 100101
            ALUOp = 5;
        } else { // is halt because unknown/illegal command
            return 1; 
        }

    } else if(ALUOp > 7 || ALUOp < 0) { // halt because unknown ALUOp
        return 1;
    }

    if(ALUSrc == 1) {
        data2 = extended_value;
    }

    ALU(data1, data2, ALUOp, ALUresult, Zero); // ALUOp is a holder for ALUControl

    return 0;
}


/* Read / Write Memory */
/* 10 Points */
int rw_memory(unsigned ALUresult,unsigned data2,char MemWrite,char MemRead,unsigned *memdata,unsigned *Mem)
{
    if((MemRead == 1 || MemWrite == 1) && (ALUresult % 4 != 0 || ALUresult>>2 >= 16384)) { // check word-alignment and address boundaries
        return 1;
    }

    if(MemRead == 1) {
        *memdata = Mem[ALUresult>>2];
    }

    if(MemWrite == 1) {
        Mem[ALUresult>>2] = data2;
    }

    return 0;
}


/* Write Register */
/* 10 Points */
void write_register(unsigned r2,unsigned r3,unsigned memdata,unsigned ALUresult,char RegWrite,char RegDst,char MemtoReg,unsigned *Reg)
{
    if(RegWrite == 1) { 

        unsigned data = 0;
        if(MemtoReg == 0) { // choose ALU
            data = ALUresult;
        }
        else if(MemtoReg == 1) { // choose Memory Data
            data = memdata;
        }

        if(RegDst == 0) { // write to rt
            Reg[r2] = data;
        }
        else if(RegDst == 1) { // write to rd
            Reg[r3] = data;
        }

    } 
}

/* PC update */
/* 10 Points */
void PC_update(unsigned jsec, unsigned extended_value,char Branch,char Jump,char Zero,unsigned *PC)
{
    // move to next instruction
    *PC = (*PC) + 4; 

    if(Branch == 1 && Zero == 1) { // Zero = 1 means A - B = 0, so they are equal
        extended_value = (extended_value<<2);
        *PC = (*PC) + extended_value;
    }

    if(Jump == 1) {
        unsigned right_cover = ((1<<28) - 1);

        jsec = (jsec<<2) & right_cover;
        unsigned PC_sig = (*PC) & (~right_cover);

        *PC = PC_sig | jsec;
    }
}