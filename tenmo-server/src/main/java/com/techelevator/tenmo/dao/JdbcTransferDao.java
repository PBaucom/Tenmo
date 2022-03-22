package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransferById(int TransferId){
        Transfer transfer = new Transfer();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                " FROM transfer WHERE transfer.transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, TransferId);
        if(results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByUserId(long userId){
        List<Transfer> transferList = new ArrayList<>();
        //try a self-join?
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                " INNER JOIN account ON account.account_id = transfer.account_from " +
                " OR account.account_id = transfer.account_to " +
                " INNER JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                " WHERE tenmo_user.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }
        return transferList;
    }

    @Override
    public void createTransfer(Transfer transfer){
        try {
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
            jdbcTemplate.update(sql,
                    transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(),
                    transfer.getAccountTo(), transfer.getAmount());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Transfer mapRowToTransfer(SqlRowSet rowSet){
        try {
            return new Transfer(rowSet.getInt("transfer_id"),
                    rowSet.getInt("transfer_type_id"),
                    rowSet.getInt("transfer_status_id"),
                    rowSet.getInt("account_from"),
                    rowSet.getInt("account_to"),
                    rowSet.getBigDecimal("amount"));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
