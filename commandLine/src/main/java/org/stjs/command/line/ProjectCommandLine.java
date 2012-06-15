package org.stjs.command.line;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.List;

public class ProjectCommandLine {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usate: CommandLine <srcDir> <outputDir>");
			return;
		}
		String path = args[0];
		if (!path.endsWith("/")) {
			path += "/";
		}
		String outputDir = args[1];
		List<File> classNames = listJavaFiles(new File(path));
		CommandLine.compile(path, classNames);
		generate(path, classNames, outputDir);
		
	}

	private static void generate(String path, List<File> files, String outputDir) {
		for (File file : files) {
			CommandLine.generate(path, file.getAbsolutePath().replace(path, "").replace(".java", "").replaceAll("/", "."), outputDir);
		}
	}

	
	private static List<File> listJavaFiles(File srcDir)  {
		List<File> files = newArrayList();
		listJavaFiles0(srcDir, files);
		return files;
	}
	
	private static void listJavaFiles0(File srcDir, List<File> output)  {
		for (File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				listJavaFiles0(file, output);
			} else {
				if (file.getName().endsWith(".java")) {
					output.add(file);
				}
			}
		}
	}
}
