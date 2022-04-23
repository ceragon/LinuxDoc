package io.github.ceragon.page;

import io.github.ceragon.page.PgTableTypes.Pgd_t;

import static io.github.ceragon.page.PgTable64Types.PAGE_SHIFT;
import static io.github.ceragon.page.PgTable64Types.PGDIR_SHIFT;
import static io.github.ceragon.page.PgTable64Types.PTRS_PER_PGD;
import static io.github.ceragon.page.PgTable64Types.PTRS_PER_PTE;

public class PgTable {
    public static final int _PAGE_PRESENT = 1;

    public static long pgdIndex(long address) {
        // PGDIR_SHIFT = 39， PTRS_PER_PGD = 0b1_1111_1111，所以虚拟地址总位数是 39 + 9 = 48
        return (address >> PGDIR_SHIFT) & (PTRS_PER_PGD - 1);
    }


    public static int pgdPresent(Pgd_t pgd) {
        return pgd_flags(pgd) & _PAGE_PRESENT;
    }

    public static long pteIndex(long address) {
        // PAGE_SHIFT = 12，PTRS_PER_PTE = 0b1_1111_1111，说明 PTE 占用的是 12 ~ 21
        return (address >> PAGE_SHIFT) & (PTRS_PER_PTE - 1);
    }
}
