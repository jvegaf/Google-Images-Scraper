package me.jvegaf;

import org.jsoup.Jsoup;

import java.io.IOException;

public class GoogleImagesScraper {
  // - Variables - //

  // Data
  String searchUrl;
  String searchHtml;
  // Max number of images can check (only so many loaded in page)
  int MaxNumImages = 25;
  // Scraper
  private final String textAtImageLeft    = "\",225,225]\n,[\"https://";


// - Main Methods - //

  // Set the search results for the google images search
  public void setSearch( String inSearchKeywords, int inNumTries )
  {
    try
    {

      // Get page url and html
      searchUrl = "https://www.google.com/search?tbm=isch&tbs=isz:m,islt:vga,iar:s,ift:jpg&q=" + convertToSearchUrl( inSearchKeywords );
      searchHtml = Jsoup.connect( searchUrl ).get().html();
      // Cut down to title/href parts so less processing
      searchHtml = cutOutMainData();
      for (int i = 0; i < 10; i++) {
        System.out.println(getImageUrl(i));
      }

    }
    catch (IOException e )
    {

      // IF num of tries is > 0, try again, otherwise recurse back up
      if ( inNumTries > 0 )
      {
        setSearch( inSearchKeywords, inNumTries - 1 );
      }

    }

  }


  // Get the n'th image's url
  public String getImageUrl( int inImageNum ) {

    String imageResultUrl = null;
    String outUrl;
    int curIndex = 0;
    int urlLeft;
    int urlRight;
    int i;

    // Go to n'th image url
    for ( i = 0; i < inImageNum; i++ )
    {
      curIndex = searchHtml.indexOf( textAtImageLeft, curIndex ) + 1;
    }

    // Cut out url
    curIndex--;
    int textAtImageLeftLen = 14;
    urlLeft = searchHtml.indexOf( textAtImageLeft, curIndex ) + textAtImageLeftLen;
    String textAtImageRight = "\",";
    urlRight = searchHtml.indexOf(textAtImageRight, urlLeft );

    outUrl = fixUnsupportedChars( searchHtml.substring( urlLeft, urlRight ) );

    return outUrl;

  }


// - Accessors - //


  // Get the page's html
  public String getHtml()
  {
    return searchHtml;
  }


// - Private Methods - //

  // Get video titles and href's from html
  private String cutOutMainData() throws IOException
  {
    String outMainData;

    // IF html does not include required text, error in retrieving html originally, so throw exception
    if (!searchHtml.contains(textAtImageLeft))
    {
      throw new IOException();
    }

    // ELSE get main data and return
    outMainData = searchHtml.substring( searchHtml.indexOf( textAtImageLeft ), searchHtml.lastIndexOf( textAtImageLeft ) + 50 );

    return outMainData;
  }


  // Change certain chars in given string to make suitable for URL search
  private String convertToSearchUrl( String inSearch )
  {
    String outSearch = inSearch;

    outSearch = outSearch.replace( "&", "%26" );
    outSearch = outSearch.replace( ",", "%2C" );
    outSearch = outSearch.replace( "\\+", "%2B" );
    outSearch = outSearch.replace( ' ', '+' );

    return outSearch;
  }


  // Change unsupported chars in image url
  private String fixUnsupportedChars( String inString )
  {
    String outString = inString;

    outString = outString.replace( "\\u003d", "=" );
    outString = outString.replace( "\\u0026", "&" );

    return outString;
  }
}
