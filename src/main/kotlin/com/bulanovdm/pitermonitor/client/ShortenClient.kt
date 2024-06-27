package com.bulanovdm.pitermonitor.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "shorten-client")
interface ShortenClient {

    @GetMapping("?url={url}")
    fun getShortUrl(@PathVariable(value = "url") url: String): String
}