package dev.enche.persistence;

import java.util.UUID;

public abstract class Identifiable {

    private UUID id = UUID.randomUUID();

    public UUID getId() { return id; }

}
