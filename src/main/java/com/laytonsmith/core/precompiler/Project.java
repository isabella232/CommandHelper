package com.laytonsmith.core.precompiler;

import com.laytonsmith.PureUtilities.Version;
import java.io.File;

/**
 * A Project is a collection of files that may or may not have a main file. A project can include other projects (or
 * project outputs) and they are as if the project is merged together into one. Generally speaking, if files are not
 * directly referenced via includes, there should be no issue with overlapping files, but if a file is included, and
 * there are similarly named files in each project, then the include must be modified, using project notation.
 */
public class Project {

	/**
	 * This is the project name, and is used for reference purposes only.
	 */
	private final String projectName;

	/**
	 * The output file name. This must end in .msc. If set to null, the project's name is used, with spaces changed to
	 * underscores, and .msc appended.
	 */
	private final String output;

	/**
	 * The main file name. Defaults to main.ms, but may be changed, and may be null, in the case that this is a library
	 * project.
	 */
	private final String main;

	/**
	 * This is the project version. The version must follow the {@link Version} symantics, as it is potentially used
	 * programmatically.
	 */
	private final Version projectVersion;

	/**
	 * The author's name. This is used for reference purposes only.
	 */
	private final String author;

	/**
	 * The project's license, i.e. Apache, MIT, GPL. This is used for reference, but can be used for code scanning tools
	 * to quickly and easily determine a project's license. May be null, though this is highly recommended to be set for
	 * projects that are being released as open source.
	 */
	private final String license;

	/**
	 * A reference to the license text in a file somewhere within the project. This may be null, but if the license is
	 * not null, this may cause a warning to be issued.
	 */
	private final File licenseText;

	public class ProjectBuilder {
		String projectName;
		String output;
		String main;
		Version version;
		String author;
		String license;
		File licenseText;
		public ProjectBuilder2 setProjectName(String projectName) {
			this.projectName = projectName;
			return new ProjectBuilder2();
		}

		public class ProjectBuilder2 {
			private ProjectBuilder2(){}
			public ProjectBuilder3 setOutput(String output) {
				ProjectBuilder.this.output = output;
				return new ProjectBuilder3();
			}
		}

		public class ProjectBuilder3 {
			private ProjectBuilder3(){}
			public ProjectBuilder4 setMain(String main) {
				ProjectBuilder.this.main = main;
				return new ProjectBuilder4();
			}
		}

		public class ProjectBuilder4 {
			private ProjectBuilder4(){}
			public ProjectBuilder5 setVersion(Version version) {
				ProjectBuilder.this.version = version;
				return new ProjectBuilder5();
			}
		}

	}
}
