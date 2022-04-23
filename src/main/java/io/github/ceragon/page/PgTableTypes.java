package io.github.ceragon.page;

public class PgTableTypes {

    public static class Pgd_t {
        public long pgd;
    }

    public static long pgdFlags(Pgd_t pgd) {
        return native_pgd_val(pgd) & PTE_FLAGS_MASK;
    }

    private static long native_pgd_val(Pgd_t pgd) {
        return pgd.pgd;
    }
}
