/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.policyengine;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ranger.plugin.policyengine.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public class PerfTestClient extends Thread {
	static final Log LOG      = LogFactory.getLog(PerfTestClient.class);

	final PerfTestEngine perfTestEngine;
	final int clientId;
	final URL requestFileURL;
	final int maxCycles;

	List<RequestData> requests = null;
	static Gson gsonBuilder  = null;

	static {

		gsonBuilder = new GsonBuilder().setDateFormat("yyyyMMdd-HH:mm:ss.SSS-Z")
				.setPrettyPrinting()
				.registerTypeAdapter(RangerAccessRequest.class, new RangerAccessRequestDeserializer())
				.registerTypeAdapter(RangerAccessResource.class, new RangerResourceDeserializer())
				.create();
	}

	public PerfTestClient(final PerfTestEngine perfTestEngine, final int clientId,  final URL requestFileURL, final int maxCycles) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("==> PerfTestClient(clientId=" + clientId + ", maxCycles=" + maxCycles +")" );
		}

		this.perfTestEngine = perfTestEngine;
		this.clientId = clientId;
		this.requestFileURL = requestFileURL;
		this.maxCycles = maxCycles;

		setName("PerfTestClient-" + clientId);
		setDaemon(true);

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== PerfTestClient(clientId=" + clientId + ", maxCycles=" + maxCycles +")" );
		}
	}

	public boolean init() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("==> init()" );
		}

		boolean ret = false;

		Reader reader = null;

		try {

			InputStream in = requestFileURL.openStream();

			reader = new InputStreamReader(in);

			Type listType = new TypeToken<List<RequestData>>() {
			}.getType();

			requests = gsonBuilder.fromJson(reader, listType);

			ret = true;
		}
		catch (Exception excp) {
			LOG.error("Error opening request data stream or loading load request data from file, URL=" + requestFileURL, excp);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception excp) {
					LOG.error("Error closing file ", excp);
				}
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== init() : " + ret );
		}
		return ret;
	}

	@Override
	public void run() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("==> run()" );
		}

		try {
			for (int i = 0; i < maxCycles; i++) {
				for (RequestData data : requests) {
					perfTestEngine.execute(data.request);
				}
			}
		} catch (Exception excp) {
			LOG.error("PerfTestClient.run() : interrupted! Exiting thread", excp);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== run()" );
		}
	}

	private class RequestData {
		public String              name;
		public RangerAccessRequest request;
		public RangerAccessResult result;
	}

	static class RangerAccessRequestDeserializer implements JsonDeserializer<RangerAccessRequest> {
		@Override
		public RangerAccessRequest deserialize(JsonElement jsonObj, Type type,
											   JsonDeserializationContext context) throws JsonParseException {
			RangerAccessRequestImpl ret = gsonBuilder.fromJson(jsonObj, RangerAccessRequestImpl.class);

			ret.setAccessType(ret.getAccessType()); // to force computation of isAccessTypeAny and isAccessTypeDelegatedAdmin

			return ret;
		}
	}

	static class RangerResourceDeserializer implements JsonDeserializer<RangerAccessResource> {
		@Override
		public RangerAccessResource deserialize(JsonElement jsonObj, Type type,
												JsonDeserializationContext context) throws JsonParseException {
			return gsonBuilder.fromJson(jsonObj, RangerAccessResourceImpl.class);
		}
	}
}
