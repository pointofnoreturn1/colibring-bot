package io.vaku.model.enm;

public enum Role {
    RESIDENT(""),
    CLEANER("клинер"),
    COOK("повар"),
    MANAGER("управляюший"),
    ADMIN("");

    private final String nameRu;

    Role(String nameRu) {
        this.nameRu = nameRu;
    }

    public String genNameRu() {
        return nameRu;
    }
}
