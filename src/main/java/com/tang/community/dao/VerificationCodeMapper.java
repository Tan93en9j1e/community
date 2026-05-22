package com.tang.community.dao;

import com.tang.community.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VerificationCodeMapper {

    int insertVerificationCode(VerificationCode verificationCode);

    VerificationCode selectByCode(String code);

    int updateStatus(int id, int status);

    VerificationCode selectByEmail(String email);

    int incrementAttemptCount(int id);

    int deleteExpiredCodes();
}
