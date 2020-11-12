package ru.my.test

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import javax.annotation.PostConstruct
import javax.sql.DataSource

@TestConfiguration
class DataLoader(private val dataSource: DataSource) {
    @PostConstruct
    fun init() {
        val resource = ClassPathResource("init.sql")
        ScriptUtils.executeSqlScript(dataSource.connection, resource)
    }
}
