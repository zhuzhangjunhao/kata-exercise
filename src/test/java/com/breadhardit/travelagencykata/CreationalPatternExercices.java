package com.breadhardit.travelagencykata;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class CreationalPatternExercices {
    /*
     * Banking accounts has movements. And each movement can be a deposit or withdrawal
     * After a few months operating, we need to create new types of movements:
     *   - TRANSFER: It's a withdrawal, bet we need the destination account number
     *   - ANNULMENT: It cancels a movement, so, we need the original movement
     */

    public interface Movement{
        String getID();
        Long getAmount();
        String getdescription();
        void apply (Account account);
    }

    @Data
    @RequiredArgsConstructor
    public static class Account {
        public static final ConcurrentHashMap<String, Movement> MOVEMENTS = new ConcurrentHashMap<>();
        final String id;
        Long balance = 0L;

        public void addMovement(Movement movement) {
            MOVEMENTS.put(movement.getID(), movement);
            movement.apply(this);
            log.info("Current balance: {}", balance);
        }
    }

    @Data
    @AllArgsConstructor
    public static abstract class BaseMovement implements Movement {
        String id;
        Long amount;
        String description;
    }


    public static class Deposit extends BaseMovement {

        Deposit(String id, Long amount, String description) {
            super(id, amount, description);
        }

        @Override
        public String getID() {
            return this.id;
        }

        @Override
        public String getdescription() {
            return this.description;
        }

        @Override
        public void apply(Account account){
            account.setBalance(account.getBalance() + getAmount());
        }
    }

    public static class Withdrawal extends BaseMovement {
        Withdrawal(String id, Long amount, String description) {
            super(id, amount, description);
        }

        @Override
        public String getID() {
            return this.id;
        }

        @Override
        public String getdescription() {
            return this.description;
        }

        @Override
        public void apply(Account account){
            account.setBalance(account.getBalance() - getAmount());
        }
    }

    public static class Transfer extends Withdrawal {
        private final String destinationAccount;
        Transfer(String id, Long amount, String description, String destinationAccount) {
            super(id, amount, description);
            this.destinationAccount = destinationAccount;
        }

        @Override
        public void apply(Account account){
            super.apply(account);
            log.info("Transferred {} to accont {}", amount, destinationAccount);
        }
    }

    public static class Annulment extends Deposit {
        private final String originalMovementId;
        Annulment(String id, Long amount, String description, String originalMovementId) {
            super(id, amount, description);
            this.originalMovementId = originalMovementId;
        }

        @Override
        public void apply(Account account){
            super.apply(account);
            log.info("Annulled movement: {}", originalMovementId);
        }
    }

    //Apuntes: Es imposible no cambiar el código al intoducir un cambio, pero con factoria es mucho más fácil realizar los cambios sin alterar código previo
    public static class Factory {
        public static Movement createMovement(String type, String id, Long amount, String description, String extra){
            switch (type.toUpperCase()){
                case "DEPOSIT":
                    return new Deposit(id, amount, description);
                case "WITHDRAWAL":
                    return new Withdrawal(id, amount, description);
                case "TRANSFER":
                    return new Transfer(id, amount, description, extra);
                case "ANNULMENT":
                    return new Annulment(id, amount, description, extra);
                default:
                    throw new IllegalArgumentException("Invalid movment type");
            }
        }
    }

    @Test
    public void test() {
        Account account = new Account(UUID.randomUUID().toString());
        account.addMovement(Factory.createMovement("DEPOSIT", "1", 1000L, "INGRESO", null));
        account.addMovement(Factory.createMovement("WITHDRAWAL", "2", 10L, "GASTOS VARIOS", null));
        account.addMovement(Factory.createMovement("TRANSFER", "3", 500L, "PAGO A JUAN", "DEST-1234"));
        account.addMovement(Factory.createMovement("ANNULMENT", "4", 500L, "REEMBOLSO", "3"));
    }
}