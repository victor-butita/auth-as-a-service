package com.authservice.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthCoreApplication

fun main(args: Array<String>) {
	runApplication<AuthCoreApplication>(*args)
}
