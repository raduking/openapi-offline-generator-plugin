package org.oogp;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/info")
public class InfoController {

	private final String name;

	public InfoController(String name) {
		this.name = name;
	}

	@SuppressWarnings("unused")
	@GetMapping("/user/{userId}")
	public ResponseEntity<UserCacheInfo> getUserCacheInfo(@PathVariable String userId) {
		UserCacheInfo result = UserCacheInfo.builder().build();
		return ResponseEntity.ok(result);
	}

	@SuppressWarnings("unused")
	@GetMapping("/users")
	public ResponseEntity<List<String>> getUsers(@RequestParam(required = false) Integer count) {
		List<String> result = List.of("one", "two", "three");
		return ResponseEntity.ok(result);
	}

	public String getName() {
		return name;
	}
}
