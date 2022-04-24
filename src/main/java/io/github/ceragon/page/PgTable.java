package io.github.ceragon.page;

import io.github.ceragon.page.MmTypes.Page;
import io.github.ceragon.page.PgTable64Types.Pte_t;
import io.github.ceragon.page.PgTableTypes.Pgd_t;

import static io.github.ceragon.page.PgTable64Types.PAGE_SHIFT;
import static io.github.ceragon.page.PgTable64Types.PGDIR_SHIFT;
import static io.github.ceragon.page.PgTable64Types.PTRS_PER_PGD;
import static io.github.ceragon.page.PgTable64Types.PTRS_PER_PTE;
import static io.github.ceragon.page.PgTable64Types.VMEMMAP_START;
import static io.github.ceragon.page.PgTableTypes.PTE_PFN_MASK;

public class PgTable {
    public static final int _PAGE_BIT_PRESENT = 0; // 虚拟内存是否在内存中。
//    #define _PAGE_BIT_PRESENT	0	/* is present */
    public static final int _PAGE_BIT_RW = 1; // 是否可写
//    #define _PAGE_BIT_RW		1	/* writeable */
    public static final int _PAGE_BIT_USER = 2; // 允许用户空间代码访问该页，否则只有内核可以访问
//    #define _PAGE_BIT_USER		2	/* userspace addressable */
//    #define _PAGE_BIT_PWT		3	/* page write through */
//    #define _PAGE_BIT_PCD		4	/* page cache disabled */
    public static final int _PAGE_BIT_ACCESSED = 5; // CPU 每次访问都会设置这个 bit 位
//    #define _PAGE_BIT_ACCESSED	5	/* was accessed (raised by CPU) */
    public static final int _PAGE_BIT_DIRTY = 6; // CPU 修改后会设置这个 bit 位
//    #define _PAGE_BIT_DIRTY		6	/* was written to (raised by CPU) */
//    #define _PAGE_BIT_PSE		7	/* 4 MB (or 2MB) page */
//    #define _PAGE_BIT_PAT		7	/* on 4KB pages */
//    #define _PAGE_BIT_GLOBAL	8	/* Global TLB entry PPro+ */
//    #define _PAGE_BIT_UNUSED1	9	/* available for programmer */
//    #define _PAGE_BIT_IOMAP		10	/* flag used to indicate IO mapping */
//    #define _PAGE_BIT_HIDDEN	11	/* hidden by kmemcheck */
//    #define _PAGE_BIT_PAT_LARGE	12	/* On 2MB or 1GB pages */
    public static final int _PAGE_BIT_NX = 63; // 不可执行。例如防止执行栈页上的代码，否则恶意代码会通过缓冲区溢出手段在栈上执行代码。
//    #define _PAGE_BIT_NX           63       /* No execute: only valid after cpuid check */

    public static final long _PAGE_PRESENT = 1L;
    public static final long _PAGE_DIRTY = 1L << _PAGE_BIT_DIRTY;

    public static long pgdIndex(long address) {
        // PGDIR_SHIFT = 39， PTRS_PER_PGD = 0b1_1111_1111，所以虚拟地址总位数是 39 + 9 = 48
        return (address >> PGDIR_SHIFT) & (PTRS_PER_PGD - 1);
    }

    /**
     * 表项是否存在
     *
     * @param pgd
     * @return
     */
    public static long pgdPresent(Pgd_t pgd) {
        return PgTableTypes.pgdFlags(pgd) & _PAGE_PRESENT;
    }

    public static long ptePresent(Pte_t a) {
        // & 0b1_0000_0001
        return PgTableTypes.pteFlags(a) & (_PAGE_PRESENT | PgTableTypes._PAGE_PROTNONE);
    }

    public static long pteDirty(Pte_t a) {
        return PgTableTypes.pteFlags(a) & _PAGE_DIRTY;
    }

    public static long pteIndex(long address) {
        // PAGE_SHIFT = 12，PTRS_PER_PTE = 0b1_1111_1111，说明 PTE 占用的是 12 ~ 21
        return (address >> PAGE_SHIFT) & (PTRS_PER_PTE - 1);
    }

    public static long ptePfn(Pte_t pte) {
        return (pte.pte & PTE_PFN_MASK) >> PAGE_SHIFT;
    }

    public static Page ptePage(Pte_t pte) {
        Object obj = (VMEMMAP_START + ptePfn(pte));
        return (Page) obj;

    }
}
