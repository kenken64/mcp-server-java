package com.mcp.server;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

public class App
{
    private static final Logger log= LoggerFactory.getLogger(App.class);
    private static final BookTools  booksTools = new BookTools();

    public static void main( String[] args )
    {
        // Stdio Server Transport 
        var transportProvider = new StdioServerTransportProvider(new ObjectMapper());
        // get sync tool
        var syncToolSpecification = getSyncToolSpecification();
        McpSyncServer syncServer = McpServer.sync(transportProvider)
            .serverInfo("java-mcp-server", "0.1")
            .capabilities(McpSchema.ServerCapabilities.builder()
                .tools(true)
                .logging()
                .build())
            .tools(syncToolSpecification, getSearchBooks(), getSearchBooksInRange())
            .build();
        log.info("Model Context Protocol (Books) Java Server started");
    }

    private static McpServerFeatures.SyncToolSpecification getSyncToolSpecification(){
        var schema = """
            {
                "type": "object",
                "id": "urn:jsonSchema:Operation",
                "properties": {
                    "operation": {
                        "type": "string",
                        "description": "The operation to perform"
                    }
                },
                "required": ["operation"]
            }
        """;

        var syncToolSpecification = new McpServerFeatures.SyncToolSpecification(
            new McpSchema.Tool("get_all_books", "to find all published books", schema),
            (exchange, arguments)-> {
                List<Books> presentations =  booksTools.getBooks();
                List<McpSchema.Content> contents = new ArrayList<>();
                for(Books presentation: presentations){
                    contents.add(new McpSchema.TextContent(presentation.toString()));
                }
                return new McpSchema.CallToolResult(contents, false);
            });
        return syncToolSpecification;
    }

    private static McpServerFeatures.SyncToolSpecification getSearchBooks(){
        var searchSchema = """
            {
                "type": "object",
                "id": "urn:jsonSchema:Operation",
                "properties": {
                    "operation": {
                        "type": "string",
                        "description": "The operation to perform"
                    },
                    "year": {
                        "type": "integer",
                        "description": "year of book"
                    }
                },
                "required": ["operation"]
            }
        """;
        var searchToolSpecification = new McpServerFeatures.SyncToolSpecification(
            new McpSchema.Tool(
                "search_books_by_year", 
                " to find books published in a specific year",  // description 
                searchSchema
            ),
            (exchange, arguments)-> {
                List<Books> presentations;
                if (arguments.containsKey("year")) {
                    // If year is provided, get its value and convert to int
                    Object yearObj = arguments.get("year");
                    int year;
                    if (yearObj instanceof Number number) {
                        year = number.intValue();
                    } else {
                        year = Integer.parseInt(yearObj.toString());
                    }
                    presentations = booksTools.getBooksByYear(year);
                } else {
                    // Otherwise get all books  
                    presentations = booksTools.getBooks();
                }
                
                List<McpSchema.Content> contents = new ArrayList<>();
                for (Books presentation : presentations) {
                    contents.add(new McpSchema.TextContent(presentation.toString()));
                }
                
                return new McpSchema.CallToolResult(contents, false);
            });
        return searchToolSpecification;
    }

    private static McpServerFeatures.SyncToolSpecification getSearchBooksInRange(){
        var searchSchema = """
            {
                "type": "object",
                "id": "urn:jsonSchema:Operation",
                "properties": {
                    "operation": {
                        "type": "string",
                        "description": "The operation to perform"
                    },
                    "fromYear": {
                        "type": "integer",
                        "description": "from published year of book"
                    },
                    "toYear": {
                        "type": "integer",
                        "description": "to published year of book"
                    }
                },
                "required": ["operation"]
            }
        """;
        var searchToolSpecification = new McpServerFeatures.SyncToolSpecification(
            new McpSchema.Tool(
                "search_books_by_year_range", 
                "to find books published within a range of years",  // description 
                searchSchema
            ),
            (exchange, arguments)-> {
                List<Books> presentations;
                if (arguments.containsKey("fromYear") && arguments.containsKey("toYear")) {
                    // If year is provided, get its value and convert to int
                    Object fromYearObj = arguments.get("fromYear");
                    Object toYearObj = arguments.get("toYear");
                    int fromYear;
                    int toYear;
                    
                    if (fromYearObj instanceof Number number) {
                        fromYear = number.intValue();
                    } else {
                        fromYear = Integer.parseInt(fromYearObj.toString());
                    }

                    if (toYearObj instanceof Number number) {
                        toYear = number.intValue();
                    } else {
                        toYear = Integer.parseInt(toYearObj.toString());
                    }
                    presentations = booksTools.getBooksByYearRange(fromYear, toYear);
                } else {
                    // Otherwise get all books  
                    presentations = booksTools.getBooks();
                }
                
                List<McpSchema.Content> contents = new ArrayList<>();
                for (Books presentation : presentations) {
                    contents.add(new McpSchema.TextContent(presentation.toString()));
                }
                
                return new McpSchema.CallToolResult(contents, false);
            });
        return searchToolSpecification;
    }
}
