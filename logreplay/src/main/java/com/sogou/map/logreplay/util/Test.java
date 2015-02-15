package com.sogou.map.logreplay.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.logprocess.log.OperationLog;
import com.sogou.map.logreplay.logprocess.processor.OperationLogProcessor;


/**
 * »’÷æΩ‚Œˆ≤‚ ‘
 */
public class Test {

	public static void main(String[] args) throws Exception {
		File src = new File("d:/6000.txt");
		File target = new File("d:/result.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(src), "UTF-8"));
		BufferedWriter writer = new BufferedWriter(new FileWriter(target));
		String line = "";
		while((line = reader.readLine()) != null) {
			try {
				OperationLog operationLog = new OperationLogProcessor().process(line);
				if(operationLog == null) {
					continue;
				}
				for(OperationRecord record: operationLog.toRecordList()) {
					writer.write(record.toString() + SystemUtils.LINE_SEPARATOR);
				}
			} catch (Exception e) {
				System.err.println(line);
				e.printStackTrace();
			}
		}
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(writer);
	}
}

