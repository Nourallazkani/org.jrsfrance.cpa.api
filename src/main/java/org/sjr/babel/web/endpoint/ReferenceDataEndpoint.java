package org.sjr.babel.web.endpoint;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("referenceData")
public class ReferenceDataEndpoint extends AbstractEndpoint {

	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Map<String, List<?>>> allLists(@RequestParam(defaultValue="false") boolean noCache) {
		if (this.refDataProvider.getReferenceData() == null || noCache) {
			refDataProvider.loadCache();
		}
		return this.refDataProvider.getReferenceData();
	}

	@RequestMapping(path = "{s}", method = RequestMethod.GET)
	public ResponseEntity<?> oneList(@PathVariable String s, @RequestParam(defaultValue="false") boolean noCache) {
		if (this.refDataProvider.getReferenceData() == null) {
			this.refDataProvider.loadCache();
		}
		else {
			if(noCache){
				this.refDataProvider.loadCache(s);
			}
		}
		Object list = this.refDataProvider.getReferenceData().get(s);
		if (list == null) {
			String body = s + " does not exists, please use : " + String.join(", ", this.refDataProvider.getReferenceData().keySet());
			return ResponseEntity.status(404).body(body);
		}
		return ResponseEntity.ok(list);
	}
}