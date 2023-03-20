# YAMLCommentWriter

[![version](https://img.shields.io/github/v/release/CarmJos/YAMLCommentWriter)](https://github.com/CarmJos/YAMLCommentWriter/releases)
[![License](https://img.shields.io/github/license/CarmJos/YAMLCommentWriter)](https://opensource.org/licenses/MIT)
[![workflow](https://github.com/CarmJos/YAMLCommentWriter/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/YAMLCommentWriter/actions/workflows/maven.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/YAMLCommentWriter/badge)](https://www.codefactor.io/repository/github/carmjos/YAMLCommentWriter)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/YAMLCommentWriter)
![](https://visitor-badge.glitch.me/badge?page_id=YAMLCommentWriter.readme)

A file writer for yaml configuration with provided comments.

## Usage
1. Implement `CommentedYAML` for writer to get original config contents and comments.
2. Use `CommentedYAMLWriter#writeWithComments(commentedYAML, file)` to write config file.

## Dependency

<details>
<summary>Maven dependency</summary>

```xml

<project>
    <repositories>
      
        <repository>
            <!--Using central repository-->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://oss.sonatype.org/content/groups/public/</url>
        </repository>
      
        <repository>
            <!--Using github packages-->
            <id>YAMLCommentWriter</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/YAMLCommentWriter</url>
        </repository>
      
    </repositories>

    <dependencies>
      
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>YAMLCommentWriter</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>
      
    </dependencies>

</project>
```

</details>

<details>
<summary>Gradle dependency</summary>

```groovy
repositories {
  
    mavenCentral() // Using central repository.
  
    // Using github packages.
    maven { url 'https://maven.pkg.github.com/CarmJos/YAMLCommentWriter' }
}

dependencies {
    api "cc.carm.lib:YAMLCommentWriter:[LATEST RELEASE]"
}
```

</details>

## Open Source License.

The project using [The MIT License](https://opensource.org/licenses/MIT) .

## Support

Many thanks to Jetbrains for kindly providing a license for me to work on this and other open-source projects.  
[![](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/CarmJos/EasySQL)