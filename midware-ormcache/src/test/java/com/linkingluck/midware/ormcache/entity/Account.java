package com.linkingluck.midware.ormcache.entity;

import com.linkingluck.midware.ormcache.AbstractEntity;
import com.linkingluck.midware.ormcache.anno.Cached;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Cached
@Entity
public class Account extends AbstractEntity<Long> {

    @Id
    private Long accountId;

    @Column
    private String account;

    @Column
    private String platCode;

    @Column
    private String pid;

    public static Account valueOf(Long accountId) {
        Account account = new Account();
        account.accountId = accountId;
        return account;
    }

    @Override
    public Long getId() {
        return accountId;
    }

    @Override
    public boolean serialize() {
        return true;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPlatCode() {
        return platCode;
    }

    public void setPlatCode(String platCode) {
        this.platCode = platCode;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
