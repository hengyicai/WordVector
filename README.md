# WordVector
Read all text files under a directory, and calculate the TF-IDF scores of each term in each file, then write the result to a output file, you can also handle the result as you want.

## Usage

```java
        String indexPath = "./index"; // path to store the lucene index files
        String docsPath = "path/to/your/files/directory";
        String outFilePath = "./tf_idf_output.txt";
        String encoding = "UTF-8"; // encoding for your files, maybe ISO-8859-1 or UTF-8
        
        WordVector wordVector = new WordVector(indexPath,docsPath,true,encoding);
        // get the result
        Map<String, HashMap> documentsScores = wordVector.TFIDFScore();
        // write to file
        Utils.write2DMapToFile(outFilePath,documentsScores);
```

You can also run it with **mvn** from the command line use some args:

1.  Run command `mvn compile` in the path `./WordVector/` where **pom.xml** stores there.

```shell
$ mvn compile
```

2.  Run command `mvn exec:java -Dexec.mainClass="GenTFIDF"` to see the usage info.

```shell
$ mvn exec:java -Dexec.mainClass="GenTFIDF"
```

3.  Run `GenTFIDF` with command-line arguments like this:

```shell
$ mvn exec:java -Dexec.mainClass="GenTFIDF" -Dexec.args="-docs DOCS_PATH [-o OUTPUT_FILE] [-e TEXT_FILE_ENCODING]"
```
	
4.  Check your output file to see the tf-idf scores of each term in each document, the terms in each document have been sorted in descending order, so you can find the most important terms to this document in the collection or corpus.


## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

