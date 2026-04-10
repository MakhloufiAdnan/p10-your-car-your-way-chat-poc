package com.openclassroom.chatpoc.conversation.enums;

public enum ConversationCategory {
    BOOKING("Réservation"),
    PAYMENT("Paiement"),
    MODIFICATION("Modification"),
    CANCELLATION("Annulation"),
    GENERAL_SUPPORT("Support général");

    private final String label;

    ConversationCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
