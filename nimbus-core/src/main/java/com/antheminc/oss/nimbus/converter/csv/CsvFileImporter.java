/**
 *  Copyright 2016-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.converter.csv;

import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.converter.FileImporter;
import com.antheminc.oss.nimbus.converter.FileParser;
import com.antheminc.oss.nimbus.converter.RowProcessable;
import com.antheminc.oss.nimbus.converter.RowProcessable.RowProcessingHandler;
import com.antheminc.oss.nimbus.converter.RowProcessable.RowErrorHandler;
import com.antheminc.oss.nimbus.converter.writer.CommandHandlingBeanWriter;
import com.antheminc.oss.nimbus.converter.writer.ModelRepositoryBeanWriter;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.config.builder.DomainConfigBuilder;
import com.antheminc.oss.nimbus.domain.model.state.repo.ModelRepository;
import com.antheminc.oss.nimbus.support.JustLogit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Enums;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>An csv file importer interface that imports data from an .csv file into
 * the provided {@link ModelRepository}.
 * 
 * @author Tony Lopez
 * @author Sandeep Mantha
 *
 */
@Getter
@Setter
public class CsvFileImporter extends FileImporter {

	public static enum ErrorHandling {
		/**
		 * <p>Re-throws any thrown data parsing exception when an error parsing
		 * row data occurs.
		 */
		SILENT,

		/**
		 * <p>Silently continues processing when an error parsing row data
		 * occurs.
		 */
		STRICT;
	}

	public static enum WriteStrategy {
		/**
		 * <p>Use Command DSL's _new implementation to write each record of row
		 * data processed.
		 */
		COMMAND_DSL,

		/**
		 * <p>Use the provided domain's {@link ModelRepository} implementation
		 * to write each record of row data processed.
		 */
		MODEL_REPOSITORY;
	}

	public static final JustLogit LOG = new JustLogit();

	public static final String ARG_ERROR_HANDLING = "errors";
	public static final String ARG_PARALLEL = "parallel";
	public static final String ARG_WRITE_STRATEGY = "writeStrategy";
	public static final String CSV = "csv";
	public static final String[] SUPPORTED_EXTENSIONS = new String[] { CSV };

	private final CommandExecutorGateway commandGateway;
	private final DomainConfigBuilder domainConfigBuilder;
	private final ObjectMapper om;

	private FileParser fileParser;

	private RowErrorHandler silentErrorHandler = (e, rowData) -> {
	};
	private RowErrorHandler strictErrorHandler = (e, rowData) -> {
		throw new FrameworkRuntimeException(e);
	};

	public CsvFileImporter(CommandExecutorGateway commandGateway, DomainConfigBuilder domainConfigBuilder,
			ObjectMapper om) {
		this.commandGateway = commandGateway;
		this.domainConfigBuilder = domainConfigBuilder;
		this.om = om;

		// TODO consider moving this to a bean
		this.fileParser = new UnivocityCsvParser(domainConfigBuilder);
	}

	@Override
	public <T> void doImport(Command command, InputStream stream) {
		prepareRowProcessing(command);
		prepareErrorHandling(command);
		getFileParser().parse(stream, command);
	}

	protected void prepareRowProcessing(Command command) {
		WriteStrategy writeStrategy = getEnumFromRequestParam(command, ARG_WRITE_STRATEGY, WriteStrategy.COMMAND_DSL);
		final RowProcessingHandler onRowProcess;
		if (WriteStrategy.COMMAND_DSL == writeStrategy) {
			onRowProcess = new CommandHandlingBeanWriter(getOm(), getCommandGateway(), command);
		} else if (WriteStrategy.MODEL_REPOSITORY == writeStrategy) {
			onRowProcess = new ModelRepositoryBeanWriter();
		} else {
			throw new UnsupportedOperationException("Write strategy for" + writeStrategy + " is not supported.");
		}
		
		RowProcessable fileParser = (RowProcessable) getFileParser();
		fileParser.onRowProcess(onRowProcess);
		fileParser.setParallel(Boolean.valueOf(command.getFirstParameterValue(ARG_PARALLEL)));
	}

	protected void prepareErrorHandling(Command command) {
		ErrorHandling errorHandling = getEnumFromRequestParam(command, ARG_ERROR_HANDLING, ErrorHandling.SILENT);
		final RowErrorHandler onErrorHandler;
		if (ErrorHandling.SILENT == errorHandling) {
			onErrorHandler = getSilentErrorHandler();
		} else if (ErrorHandling.STRICT == errorHandling) {
			onErrorHandler = getStrictErrorHandler();
		} else {
			throw new UnsupportedOperationException("Error handling for " + errorHandling + " is not supported.");
		}

		((RowProcessable) getFileParser()).onRowProcessError(onErrorHandler);
	}

	@SuppressWarnings("unchecked")
	private <T extends Enum<?>> T getEnumFromRequestParam(Command command, String argumentName, T defaultValue) {
		String sRequestParamValue = command.getFirstParameterValue(argumentName);
		if (null == sRequestParamValue) {
			return defaultValue;
		}
		return (T) Enums.getIfPresent(defaultValue.getClass(), sRequestParamValue.toUpperCase())
				.or(defaultValue);
	}
	
	@Override
	public boolean supports(String extension) {
		return ArrayUtils.contains(SUPPORTED_EXTENSIONS, extension);
	}
}
