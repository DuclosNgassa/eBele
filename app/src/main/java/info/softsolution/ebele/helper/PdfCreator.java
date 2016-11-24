package info.softsolution.ebele.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import info.softsolution.ebele.model.Blutdruck;


public class PdfCreator
{
	private Context context ;
	private static final String FILE = "Blutdruck.pdf";
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private java.io.OutputStream outputStream;
	
	public PdfCreator(Context context)
	{
		this.context = context;
	}
	
	public  void create(List<Blutdruck> listBlutdruck, String name)
	{
		try
		{
			
			File cacheFile = new File(context.getCacheDir() + File.separator + FILE);
			cacheFile.createNewFile();
			outputStream = new FileOutputStream(cacheFile);
			Document document= new Document();
			PdfWriter.getInstance(document, outputStream);
			document.open();
			addMetaData(document, name);
			addTitlePage(document, name);
			addContent(document, name, listBlutdruck);
			document.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create a new file with the given name and content, in this application's
	 * cache directory.
	 * 
	 * @param context
	 *            - Context - context to use.
	 * @param fileName
	 *            - String - the name of the file to create.
	 * @param content
	 *            - String - the content to put in the new file.
	 * @throws IOException
	 */
	public static void createCachedFile(Context context, String fileName,
			String content) throws IOException {

		File cacheFile = new File(context.getCacheDir() + File.separator
				+ fileName);

		cacheFile.createNewFile();

		FileOutputStream fos = new FileOutputStream(cacheFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
		PrintWriter pw = new PrintWriter(osw);

		pw.println(content);

		pw.flush();
		pw.close();
	}

	/**
	 * Returns an intent that can be used to launch Gmail and send an email with
	 * the specified file from this application's cache attached.
	 * 
	 * @param context
	 *            - Context - the context to use.
	 * @param email
	 *            - String - the 'to' email address.
	 * @param subject
	 *            - String - the email subject.
	 * @param body
	 *            - String - the email body.
	 * @param fileName
	 *            - String - the name of the file in this application's cache to
	 *            attach to the email.
	 * @return An Intent that can be used to launch the Gmail composer with the
	 *         specified file attached.
	 */
	public static Intent getSendEmailIntent(Context context, String email,
			String subject, String body, String fileName) {

		final Intent emailIntent = new Intent(Intent.ACTION_SEND);

		// Explicitly only use Gmail to send
		emailIntent.setClassName("com.google.android.gm",
				"com.google.android.gm.ComposeActivityGmail");

		emailIntent.setType("plain/text");

		// Add the recipients
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { email });

		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

		emailIntent.putExtra(Intent.EXTRA_TEXT, body);

		// Add the attachment by specifying a reference to our custom
		// ContentProvider
		// and the specific file of interest
		emailIntent.putExtra(
				Intent.EXTRA_STREAM,
				Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/"
						+ fileName));

		return emailIntent;
	}
	
	
//	public void read()
	
	public  void addMetaData(Document document, String name)
	{
		document.addTitle("Blutdruckwerte");
		document.addAuthor(name);
		document.addCreator(name);
		document.addCreationDate();
	}
	
	public  void addTitlePage(Document document, String name) throws DocumentException
	{
		Paragraph preface = new Paragraph();
		//leere Zeile
		addEmptyLine(preface, 1);
		//Header
		preface.add(new Paragraph("Blutdruckswerte von Frau " + name, catFont));
		
		addEmptyLine(preface, 3);
		
		//neue Seite
		document.newPage();
	}

	public  void addContent(Document document, String name, List<Blutdruck> listBlutdruck) throws DocumentException
	{
		Anchor anchor = new Anchor("Blutdruckswerte von Frau " + name, catFont);
		
		anchor.setName("Blutdruckswerte von Frau");
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		
		Paragraph subPara = new Paragraph(" ", subFont);

		addEmptyLine(subPara, 3);
		
		subPara = new Paragraph("Tabelle", subFont);
		
		Section subCatPart = catPart.addSection(subPara);
		
		Paragraph paragraph = new Paragraph();
		addEmptyLine(paragraph, 3);
		
		//Tabelle hinzufuegen
		createTable(subCatPart, listBlutdruck);
		
		//All in documen hinzufuegen
		document.add(catPart);
		
	}
	
	private  void createTable(Section subCatPart, List<Blutdruck> listBlutdruck) throws BadElementException
	{
		PdfPTable table = new PdfPTable(3);

		PdfPCell c1 = new PdfPCell(new Phrase("Ersfasst am", subFont));
		
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Systolisch", subFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Diastolisch", subFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		table.setHeaderRows(1);
		
		for(Blutdruck blutdruck : listBlutdruck)
		{
			table.addCell(blutdruck.getCreated_at());
			table.addCell(blutdruck.getSystolischString());
			table.addCell(blutdruck.getDiastolischString());
		}
		
		subCatPart.add(table);
	}
	
	public  void addEmptyLine(Paragraph paragraph, int number)
	{
		for(int i = 0; i < number; i++)
		{
			paragraph.add(new Paragraph(" "));
		}
	}
	
}
