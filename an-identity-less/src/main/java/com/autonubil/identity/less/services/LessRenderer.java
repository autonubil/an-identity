package com.autonubil.identity.less.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.sommeri.less4j.Less4jException;
import com.github.sommeri.less4j.LessCompiler;
import com.github.sommeri.less4j.LessCompiler.CompilationResult;
import com.github.sommeri.less4j.core.ThreadUnsafeLessCompiler;

@Service
public class LessRenderer {

	public String render(File file, Map<String,String> variables) throws IOException, Less4jException {
		LessCompiler compiler = new ThreadUnsafeLessCompiler();
		FileOutputStream fos = new FileOutputStream(file.getParentFile()+"/variables.less");
		for(Map.Entry<String,String> e : variables.entrySet()) {
			fos.write((e.getKey()+" : "+e.getValue()+";\n").getBytes("utf-8"));
		}
		fos.flush();
		fos.close();
		CompilationResult compilationResult = compiler.compile(file);
		return "/* rendered: "+new Date()+" */\n"+compilationResult.getCss();
	}
	
	
}
