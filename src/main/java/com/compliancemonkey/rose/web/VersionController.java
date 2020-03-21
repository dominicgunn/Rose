package com.compliancemonkey.rose.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class VersionController {

	@GetMapping("/version")
	public Flux<Version> getVersion() {
		return Flux.just(new Version("0.0.1"));
	}

	private static class Version {
		private final String version;

		private Version(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}
	}
}
