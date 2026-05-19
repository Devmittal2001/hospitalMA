package payment;

/*
We are developing a payment transaction monitoring system that tracks accounts and their transactions.
The system can compute each account's current balance and basic statistics.

Definitions:

* An "account" has a unique accountId and an owner name.
* A "transaction" represents money moving in or out of an account.
  - CREDIT increases the account balance.
  - DEBIT decreases the account balance.
* "AccountManager" manages accounts and transactions and provides balance-related methods.

To begin with, we present you with two tasks:

1-1) Read through and understand the code below. Please take as much time as necessary, and feel free to run it.

1-2) The test for AccountManager is not passing due to a bug in the code.
     Make the necessary changes to AccountManager to fix the bug.
*/

/*
We are extending our payment transaction monitoring system to support
basic analytics over transactions.

For this task, we want to calculate the average transaction amount per account.

2) Implement the function getAverageTransactionAmountByAccount in AccountManager that returns
the average transaction amount for each account.

Requirements:

- The result should associate each accountId with the average amount of its transactions.
- Both CREDIT and DEBIT transactions should be considered.
- Transaction amounts should be treated as absolute values when calculating averages.
- Accounts with no transactions should not appear in the result.
- Transactions always refer to valid accounts.

To assist you in testing this new function, we have provided the
testGetAverageTransactionAmountByAccount test.
*/

/*
We are extending our payment transaction monitoring system to calculate
transaction fees based on business rules.

Each account is charged transaction fees as follows:

- The first 3 transactions for an account are FREE.
- From the 4th transaction onward:
  - CREDIT transactions cost a flat fee of $1.
  - DEBIT transactions cost a flat fee of $2.

Transactions must be processed in chronological order (by timestamp).
Fees are applied per account independently.

3) Implement getTransactionFees() in AccountManager. The function should
return a map associating each accountId with the total transaction
fees charged to that account.

To assist you in testing this new function, we have provided the
testGetTransactionFees function.
*/

import java.util.*;

enum TransactionType {
    CREDIT,
    DEBIT
}

class Account {
    int accountId;
    String ownerName;

    Account(int accountId, String ownerName) {
        this.accountId = accountId;
        this.ownerName = ownerName;
    }
}

class Transaction {
    int transactionId;
    int accountId;
    TransactionType type;
    double amount;     // Always positive in inputs
    long timestampSec; // Unix-style seconds (monotonic for tests)

    Transaction(int transactionId, int accountId, TransactionType type, double amount, long timestampSec) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestampSec = timestampSec;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public long getTimestampSec() {
        return timestampSec;
    }

    public void setTimestampSec(long timestampSec) {
        this.timestampSec = timestampSec;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

class AccountManager {
    Map<Integer, Account> accounts = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    void addAccount(Account account) {
        if (account == null) {
            return;
        }
        accounts.putIfAbsent(account.accountId, account);
    }

    void addTransaction(Transaction tx) {
        if (tx == null) {
            return;
        }
        if (!accounts.containsKey(tx.accountId)) {
            return;
        }
        transactions.add(tx);
    }

    // Returns the current balance for the given accountId.
    public double getBalance(int accountId) {
        double balance = 0.0;

        for (Transaction tx : transactions) {
            if (tx.getAccountId() == accountId) {
                if (tx.getType() == TransactionType.CREDIT) {
                    balance += tx.getAmount();
                } else {
                    balance -= tx.getAmount();
                }
            }
        }

        return balance;
    }

    public Map<Integer, Double> getAverageTransactionAmountByAccount() {
        Map<Integer, Double> sumByAccount = new HashMap<>();
        Map<Integer, Integer> countByAccount = new HashMap<>();

        for (Transaction tx : transactions) {
            int accountId = tx.getAccountId();
            double amount = Math.abs(tx.getAmount());

            sumByAccount.put(accountId, sumByAccount.getOrDefault(accountId, 0.0) + amount);
            countByAccount.put(accountId, countByAccount.getOrDefault(accountId, 0) + 1);
        }

        Map<Integer, Double> result = new HashMap<>();
        for (Integer accountId : sumByAccount.keySet()) {
            result.put(accountId, sumByAccount.get(accountId) / countByAccount.get(accountId));
        }

        return result;
    }

    public Map<Integer, Double> getTransactionFees() {
        Map<Integer, List<Transaction>> txByAccount = new HashMap<>();

        for (Transaction tx : transactions) {
            txByAccount.computeIfAbsent(tx.getAccountId(), k -> new ArrayList<>()).add(tx);
        }

        Map<Integer, Double> fees = new HashMap<>();

        for (Integer accountId : accounts.keySet()) {
            fees.put(accountId, 0.0);
        }

        for (Map.Entry<Integer, List<Transaction>> entry : txByAccount.entrySet()) {
            int accountId = entry.getKey();
            List<Transaction> accountTxs = new ArrayList<>(entry.getValue());

            accountTxs.sort(
                    Comparator.comparingLong(Transaction::getTimestampSec)
                            .thenComparingInt(Transaction::getTransactionId)
            );

            double totalFee = 0.0;

            for (int i = 3; i < accountTxs.size(); i++) {
                Transaction tx = accountTxs.get(i);
                if (tx.getType() == TransactionType.CREDIT) {
                    totalFee += 1.0;
                } else {
                    totalFee += 2.0;
                }
            }

            fees.put(accountId, totalFee);
        }

        return fees;
    }
}

public class Main {

    public static void main(String[] args) {
        testGetBalance_basic();
        testGetBalance_multipleAccounts();
        testGetAverageTransactionAmountByAccount();
        testGetTransactionFees();
        System.out.println("All tests passed.");
    }

    private static void assertAlmost(double expected, double actual, double eps) {
        assert Math.abs(expected - actual) <= eps :
                "Expected " + expected + " but got " + actual;
    }

    public static void testGetBalance_basic() {
        System.out.println("Running testGetBalance_basic");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));

        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));

        // Expected balance: 100 - 30 - 20 + 10 = 60
        assertAlmost(60.0, mgr.getBalance(1), 0.0001);
    }

    public static void testGetBalance_multipleAccounts() {
        System.out.println("Running testGetBalance_multipleAccounts");
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));

        mgr.addTransaction(new Transaction(201, 1, TransactionType.CREDIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(203, 1, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(204, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(205, 2, TransactionType.DEBIT, 14.5, 2020));

        // Account 1: 50 - 10 = 40
        assertAlmost(40.0, mgr.getBalance(1), 0.0001);
        // Account 2: 80 - 5.5 - 14.5 = 60
        assertAlmost(60.0, mgr.getBalance(2), 0.0001);
    }

    public static void testGetAverageTransactionAmountByAccount() {
        System.out.println("Running testGetAverageTransactionAmountByAccount");
        AccountManager mgr = new AccountManager();

        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Charlie")); // no transactions

        // Account 1: 100, 30, 20, 10 => avg = 160/4 = 40
        mgr.addTransaction(new Transaction(101, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(102, 1, TransactionType.DEBIT, 30.0, 1010));
        mgr.addTransaction(new Transaction(103, 1, TransactionType.DEBIT, 20.0, 1020));
        mgr.addTransaction(new Transaction(104, 1, TransactionType.CREDIT, 10.0, 1030));

        // Account 2: 80, 5.5, 14.5 => avg = 100/3 = 33.333...
        mgr.addTransaction(new Transaction(201, 2, TransactionType.CREDIT, 80.0, 2005));
        mgr.addTransaction(new Transaction(202, 2, TransactionType.DEBIT, 5.5, 2015));
        mgr.addTransaction(new Transaction(203, 2, TransactionType.DEBIT, 14.5, 2020));

        Map<Integer, Double> avg = mgr.getAverageTransactionAmountByAccount();

        assertAlmost(40.0, avg.get(1), 0.0001);
        assertAlmost(33.3333, avg.get(2), 0.0001);

        // Account 3 has no transactions -> should not be present
        assert !avg.containsKey(3) : "Key 3 should not be added in avg since it has 0 transactions.";
    }

    public static void testGetTransactionFees() {
        System.out.println("Running testGetTransactionFees");
        AccountManager mgr = new AccountManager();

        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));
        mgr.addAccount(new Account(3, "Jane"));

        // Account 1: 5 transactions
        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 100.0, 1000));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 20.0, 1010));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.CREDIT, 10.0, 1020));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.DEBIT, 5.0, 1030));  // fee: $2
        mgr.addTransaction(new Transaction(5, 1, TransactionType.CREDIT, 7.0, 1040)); // fee: $1

        // Account 2: 4 transactions
        mgr.addTransaction(new Transaction(6, 2, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(7, 2, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(8, 2, TransactionType.CREDIT, 20.0, 2020));
        mgr.addTransaction(new Transaction(9, 2, TransactionType.DEBIT, 5.0, 2030)); // fee: $2

        // Account 3: 4 transactions
        mgr.addTransaction(new Transaction(26, 3, TransactionType.DEBIT, 50.0, 2000));
        mgr.addTransaction(new Transaction(27, 3, TransactionType.DEBIT, 10.0, 2010));
        mgr.addTransaction(new Transaction(28, 3, TransactionType.CREDIT, 20.0, 2020)); // should be 4th → $1
        mgr.addTransaction(new Transaction(29, 3, TransactionType.DEBIT, 5.0, 2005));

        Map<Integer, Double> fees = mgr.getTransactionFees();

        // Account 1: $2 + $1 = $3
        assertAlmost(3.0, fees.get(1), 0.0001);

        // Account 2: $2
        assertAlmost(2.0, fees.get(2), 0.0001);

        // Account 3: chronological order is 2000, 2005, 2010, 2020 -> 4th is CREDIT => $1
        assertAlmost(1.0, fees.get(3), 0.0001);
    }
}
