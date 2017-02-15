import java.io.*;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Index files under a directory and calculate the document vector of each
 * document
 */
public class WordVector {
	private String indexPath; // path to directory where you want to store the
								// index file
	private String docsPath; // path to directory where your documents store
								// there
	private boolean create = true; // See the comments below
	private String encoding = "UTF-8";
	private HashMap<String, String> idMapName; // map that contains the mapping
												// of document's id and its
												// filename.

	public WordVector(String indexPath, String docsPath, boolean create, String encoding) {
		this.indexPath = indexPath;
		this.docsPath = docsPath;
		this.create = create;
		this.encoding = encoding;
		this.idMapName = new HashMap<String, String>();
	}

	/**
	 * index the documents in the given docsPath
	 */
	private boolean index() {
		// check the parameters
		if (this.indexPath != null && this.docsPath != null && this.encoding != null) {
			File docsFile = new File(docsPath);
			if (docsFile.exists() && docsFile.canRead()) {
				Date start = new Date();
				try {
					System.out.println("Indexing to directory \'" + indexPath + "\'...");
					FSDirectory e = FSDirectory.open(new File(indexPath));
					// We use the default stop words list here, you can create
					// your own stop words list file to get a better result
					IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,
							new StopAnalyzer(Version.LUCENE_36, StopAnalyzer.ENGLISH_STOP_WORDS_SET));
					if (create) {
						// Create a new index in the directory, removing any
						// previously indexed documents.
						iwc.setOpenMode(OpenMode.CREATE);
					} else {
						// Add new documents to an existing index
						iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
					}

					// Optional: for better indexing performance, if you
					// are indexing many documents, increase the RAM
					// buffer. But if you do this, increase the max heap
					// size to the JVM (eg add -Xmx512m or -Xmx1g):
					//
					// iwc.setRAMBufferSizeMB(256.0);

					IndexWriter writer = new IndexWriter(e, iwc);
					// use the writer to index the documents under the docsFile
					// (batch)
					indexDocs(writer, docsFile);
					writer.close();
					Date end = new Date();
					System.out.println(
							"The indexing process used " + (end.getTime() - start.getTime()) + " total milliseconds");
					return true;
				} catch (IOException e) {
					System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
				}
			} else {
				System.out.println("Document directory \'" + docsFile.getAbsolutePath()
						+ "\' does not exist or is not readable, please check the path");
			}
		}
		return false;
	}

	public Map<String, HashMap> TFIDFScore() {
		if (!this.index()) {
			System.err.println("Sorry! The documents' indexing process failed! Check the possible reasons please!");
			return null;
		}
		int numberOfDocs = this.idMapName.size();
		System.out.println("numberOfDocs is " + numberOfDocs);
		Map<String, HashMap> scoreMap = new HashMap<String, HashMap>();

		try {
			IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(indexPath)));
			int i = 0;
			for (int k = 0; k < numberOfDocs; k++) {
				// each iteration will process one document

				int freq[]; // a vector represents the terms frequencies in
							// current document
				String terms[]; // a vector represents the terms in current
								// document

				HashMap<String, Float> wordMap = new HashMap<String, Float>();

				// k is the docNumber, this method returns a term frequency
				// vector for the specified document and field.
				TermFreqVector termsFreq = indexReader.getTermFreqVector(k, "contents");
				String document_Id = indexReader.document(k).get("documentId");

				freq = termsFreq.getTermFrequencies(); // TF
				terms = termsFreq.getTerms();

				int numberOfTerms = terms.length;

				// DefaultSimilarity is the default scoring implementation(
				// using TF-IDF ).
				DefaultSimilarity defaultSimilarity = new DefaultSimilarity();

				for (i = 0; i < numberOfTerms; i++) {
					// each iteration will process a term
					int numberOfDocsContainTerm = indexReader.docFreq(new Term("doccontent", terms[i]));
					// defaultSimilarity.tf returns a score factor based on a
					// term's within-document frequency
					// this method implemented as sqrt(freq).
					float tf = defaultSimilarity.tf(freq[i]);
					// defaultSimilarity.idf returns a score factor based on the
					// term's document frequency
					// this method implemented as
					// log(numberOfDocs/(numberOfDocsContainTerm+1)) + 1.
					float idf = defaultSimilarity.idf(numberOfDocsContainTerm, numberOfDocs);
					wordMap.put(terms[i], (tf * idf));

				}
				scoreMap.put(document_Id + "#" + this.idMapName.get(document_Id), wordMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scoreMap;
	}

	/**
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param docsFile
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 *             IOException If there is a low-level I/O error
	 */
	private void indexDocs(IndexWriter writer, File docsFile) throws IOException {
		// We do not try to index files that cannot be read
		if (docsFile.canRead()) {
			// docsFile is a Directory
			if (docsFile.isDirectory()) {
				String[] fis = docsFile.list();
				// an IO error could occur
				// if this docsFile is a empty directory, we just skip it
				if (fis != null) {
					for (int doc = 0; doc < fis.length; ++doc) {
						// index recursively
						indexDocs(writer, new File(docsFile, fis[doc]));
					}
				}
			} else {
				// docsFile is a file

				// make a new, empty document
				Document document = new Document();

				// get the file's name and generate a unique id for it, then,
				// add the <id,name> pair to the map
				String fileName = docsFile.getName();
				String id = UUID.randomUUID().toString();
				idMapName.put(id, fileName);

				document.add(new Field("contents",
						new StringReader(
								Utils.readContents(docsFile, this.encoding).replaceAll("\\d+(?:[.,]\\d+)*\\s*", "")),
						Field.TermVector.YES));
				document.add(new Field("documentId", id, Field.Store.YES, Field.Index.NO, Field.TermVector.NO));

				if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					System.out.println("adding " + docsFile);
					writer.addDocument(document);
				} else {
					System.out.println("updating " + docsFile);
					writer.updateDocument(new Term("path", docsFile.getPath()), document);
				}

			}
		}
	}
}
