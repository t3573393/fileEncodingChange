# fileEncodingChange

this tools used for detact the file encoding and change the file to target  encoding .

It aim to  the file encoding change.

# usage

this lib include two ways:

## one:
use this by a executable jar in command line model:

commands usage->

	  java -jar fileEncodingChange.jar [-e ${target encoding}] [-exts ${file extensions}] [-nb ${clear the bom header}] [-l ${local encoding}] ${inputPath} [$outputPath]
	
	  -e :change the file to the target encoding
	  -exts: the file extensions to filter the change files
	  -nb: clear the bom header, default-value true (clear the UTF-8 BOM header)
    -l: the local file encoding, usually it is the system encoding
	  inputPath: the file absolute path to change encoding
    outputPath: the result file to be save

besides you can use a file named tools.properties to set the params:

		intputPath: the file absolute path to change encoding
		outputPath: the result file to be save
		targetEncoding: the same with the -e
		extensions: the same with the -exts
		local: the same with the -l
		needClearBOM: the same with the -nb

    
## two
use the maven plugin to do, but the plugin you should put to your private respository. It is not in the center repo.

example:

			<plugin>
				<groupId>org.fartpig</groupId>
				<artifactId>fileEncodingChange-maven-plugin</artifactId>
				<version>0.1.0-RELEASE</version>
				<executions>
					<execution>
						<id>test-fileEncodingChange</id>
						<goals>
							<goal>
								convert
							</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<inputPath>***</inputPath>
					<targetEncoding>UTF-8</targetEncoding>
				</configuration>
			</plugin>
