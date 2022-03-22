package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    public Transfer getTransferById(int TransferId);

    public List<Transfer> getTransfersByUserId(long userId);

    public void createTransfer(Transfer transfer);
}
