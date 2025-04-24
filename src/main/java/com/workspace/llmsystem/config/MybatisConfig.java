package com.workspace.llmsystem.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan({"com.workspace.llmsystem.mapper" , "com.workspace.llmsystem.dao"})
public class MybatisConfig {
}
