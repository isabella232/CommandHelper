/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.laytonsmith.core.precompiler;

import com.laytonsmith.PureUtilities.Common.FileUtil;
import com.laytonsmith.PureUtilities.Common.OSUtils;
import com.laytonsmith.PureUtilities.Preferences;
import com.laytonsmith.abstraction.StaticLayer;
import com.laytonsmith.core.MethodScriptCompiler;
import com.laytonsmith.core.ParseTree;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.ConfigCompileException;
import com.laytonsmith.core.exceptions.ConfigCompileGroupException;
import com.laytonsmith.core.functions.Scheduling;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author caismith
 */
public class Precompiler {

	//***************************************************************************************
	// View Code
	//***************************************************************************************

	private static final Preferences.Preference MAIN = new Preferences.Preference("main", "main.ms",
			Preferences.Type.FILE, "The name of the main file. This is the entry point into the program.");
	private static final Preferences.Preference LIBS = new Preferences.Preference("libs", "", Preferences.Type.STRING,
			"Other projects may be included, either msc executables or other project directories. This should be a"
					+ " comma separated list of file paths.");
	private static final Preferences.Preference OUTPUT = new Preferences.Preference("output", "", Preferences.Type.FILE,
			"The output executable file. The file name MUST end in .msc.");

	/**
	 * Returns the
	 * @return
	 */
	private static Preferences getDefaultPrefs() {
		List<Preferences.Preference> prefs = new ArrayList<>();
		prefs.add(MAIN);
		prefs.add(LIBS);
		prefs.add(OUTPUT);
		return new Preferences("MethodScript Compiler", Logger.global, prefs, "This provides the"
				+ " compilation parameters for the project. See each configuration setting for more details.");
	}

	/**
	 * Given a project directory and config file name, builds the project.
	 * @param projectDirectory
	 * @param configFile
	 * @throws IOException
	 * @throws com.laytonsmith.core.exceptions.ConfigCompileException
	 * @throws com.laytonsmith.core.exceptions.ConfigCompileGroupException
	 */
	public static void compile(File projectDirectory, String configFile) throws IOException, ConfigCompileException,
			ConfigCompileGroupException {
		if(!new File(projectDirectory, configFile).exists()) {
			System.out.println("Could not find config file, cannot continue.");
			System.exit(1);
		}
		Preferences pref = getDefaultPrefs();
		pref.init(new File(projectDirectory, configFile));
		File main = pref.getFilePreference(MAIN.name);
		String libsS = pref.getStringPreference(LIBS.name);
		List<File> libs = Stream.of(libsS.split(",")).map(s -> new File(s)).collect(Collectors.toList());
		File output = pref.getFilePreference(OUTPUT.name);
		if(!output.getName().endsWith(".msc")) {
			System.out.println("The output file name must end in .msc");
			System.exit(1);
		}
		new Precompiler(projectDirectory, main, libs, output).compile();
	}

	/**
	 * Initializes the project directory, creating the base files necessary. Nothing that already exists
	 * will be overwritten.
	 * @param directory
	 * @throws IOException
	 */
	public static void initialize(File directory) throws IOException {
		directory.mkdirs();
		Preferences pref = getDefaultPrefs();
		File config = new File(directory, "build.ini");
		if(config.exists()) {
			System.out.println("Build file already exists, refusing to continue");
			System.exit(1);
		}
		pref.init(config);
		for(String f : new String[]{"main.ms", "auto_include.ms"}) {
			try {
				initializeFile(new File(directory, f), false);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void initializeFile(File f, boolean force) throws IOException {
		if(f.exists() && !force) {
			System.out.println(f + " already exists, refusing to create");
			throw new IOException();
		}
		f.createNewFile();
		f.setExecutable(true);
		String li = OSUtils.GetLineEnding();
		FileUtil.write("#!/usr/bin/env /usr/local/bin/mscript"
				+ li
				+ "<!" + li
				+ "\tstrict;" + li
				+ "\tname: " + f.getName() + ";" + li
				+ "\tauthor: " + StaticLayer.GetConvertor().GetUser(null) + ";" + li
				+ "\tcreated: " + new Scheduling.simple_date().exec(Target.UNKNOWN, null, new CString("yyyy-MM-dd", Target.UNKNOWN)).val() + ";" + li
				+ "\tdescription: " + ";" + li
				+ ">" + li + li, f, true);
	}

	//****************************************************************************************
	// End View Code
	//****************************************************************************************

	private final File project;
	private final File main;
	private final List<File> libs;
	private final File output;

	public Precompiler(File project, File main, List<File> libs, File output) {
		this.project = project;
		this.main = main;
		this.libs = libs;
		this.output = output;
	}

	public void compile() throws IOException, ConfigCompileException, ConfigCompileGroupException {
		Map<File, ParseTree> includeMap = new HashMap<>();
		compile(main, includeMap);
	}

	private ParseTree compile(File file, Map<File, ParseTree> includes) throws IOException, ConfigCompileException, ConfigCompileGroupException {
		if(!includes.containsKey(file)) {
			MethodScriptCompiler.compile(MethodScriptCompiler.lex(FileUtils.readFileToString(file), file, true));

		}
		return includes.get(file);
	}
}
