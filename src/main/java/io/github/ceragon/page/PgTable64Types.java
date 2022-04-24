package io.github.ceragon.page;

public class PgTable64Types {
    public static final int PGDIR_SHIFT = 39;
    public static final int PTRS_PER_PGD = 512;
    public static final int PAGE_SHIFT = 12;
    public static final int PTRS_PER_PTE = 512;

    public static final long VMEMMAP_START = 0;
    public static class Pte_t {
        public long pte;
    }
}
