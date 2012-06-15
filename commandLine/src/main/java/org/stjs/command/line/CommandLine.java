package org.stjs.command.line;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.stjs.generator.GenerationDirectory;
import org.stjs.generator.Generator;
import org.stjs.generator.GeneratorConfiguration;
import org.stjs.generator.GeneratorConfigurationBuilder;

import com.google.common.base.Throwables;

public class CommandLine {

	public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException {
		if (args.length != 2) {
			System.err.println("Usate: CommandLine <srcDir> <classQualfiiedName>");
			return;
		}
		String path = args[0];
		String fileName = args[1];
		File sourceFile = new File(path+"/"+fileName.replaceAll("\\.", "/")+".java");
		compile(path, Collections.singletonList(sourceFile));
		generate(path, fileName, path);
	}

	static void generate(final String path, final String className, String outputDir) {
		try {
			ClassLoader builtProjectClassLoader = new URLClassLoader(new URL[]{new File(path).toURI().toURL()}, Thread.currentThread().getContextClassLoader());
			File sourceFolder = new File(path);
			GenerationDirectory targetFolder = new GenerationDirectory(new File(outputDir), null, null);
			File generationFolder = targetFolder.getAbsolutePath();
	
			GeneratorConfigurationBuilder configBuilder = new GeneratorConfigurationBuilder();
			configBuilder.allowedPackage(builtProjectClassLoader.loadClass(className).getPackage().getName());
			GeneratorConfiguration configuration = configBuilder.build();
			new Generator().generateJavascript(builtProjectClassLoader,
					className, sourceFolder, targetFolder,
					generationFolder, configuration);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	static void compile(final String path, final List<File> sourceFiles) {
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(
					null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
					.getJavaFileObjectsFromFiles(sourceFiles);
			compiler.getTask(null, fileManager, null, null, null, compilationUnits1)
					.call();

			fileManager.close();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		
	}

}
