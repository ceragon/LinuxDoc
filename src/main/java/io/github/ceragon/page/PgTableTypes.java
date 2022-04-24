package io.github.ceragon.page;

import io.github.ceragon.page.PgTable64Types.Pte_t;

public class PgTableTypes {
    public static final long PTE_PFN_MASK = (~(1L << 12) - 1) & ((1L << 46) - 1);
    public static final long PTE_FLAGS_MASK = ~PTE_PFN_MASK;

    public static final long _PAGE_PROTNONE = 1 << 8;

    public static class Pgd_t {
        //
        public long pgd;
    }


    /**
     * 完整的有效位是 FFFF_FFFF_FFFF_FFFF，其中 0~11 与 46 ~ 63 属于表示状态的区间
     *
     * @param pgd
     * @return
     */
    public static long pgdFlags(Pgd_t pgd) {
        return native_pgd_val(pgd) & PTE_FLAGS_MASK;
    }
    public static long pteFlags(Pte_t pte) {
        return pte.pte & PTE_FLAGS_MASK;
    }

    private static long native_pgd_val(Pgd_t pgd) {
        return pgd.pgd;
    }
}
