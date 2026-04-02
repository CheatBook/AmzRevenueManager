import { APIGatewayProxyEvent, APIGatewayProxyResult } from "aws-lambda";
import Busboy from "busboy";
import { SettlementParser } from "../services/SettlementParser";
import { SettlementRepository } from "../services/SettlementRepository";

const repository = new SettlementRepository();

/**
 * Parses multipart/form-data from API Gateway event.
 */
const parseMultipart = (
  event: APIGatewayProxyEvent,
): Promise<{ fileContent: string; filename: string }> => {
  return new Promise((resolve, reject) => {
    const contentType =
      event.headers["content-type"] || event.headers["Content-Type"];
    if (!contentType) {
      return reject(new Error("Missing Content-Type header"));
    }

    const busboy = Busboy({ headers: { "content-type": contentType } });
    let fileContent = "";
    let filename = "";

    busboy.on("file", (fieldname, file, info) => {
      const { filename: name, encoding, mimeType } = info;
      filename = name;
      file.on("data", (data) => {
        fileContent += data.toString();
      });
    });

    busboy.on("finish", () => {
      if (!fileContent) {
        reject(new Error("No file found in request"));
      } else {
        resolve({ fileContent, filename });
      }
    });

    busboy.on("error", (err) => reject(err));

    const body = event.isBase64Encoded
      ? Buffer.from(event.body || "", "base64")
      : event.body || "";
    busboy.end(body);
  });
};

export const handler = async (
  event: APIGatewayProxyEvent,
): Promise<APIGatewayProxyResult> => {
  console.log("Received event:", JSON.stringify(event, null, 2));

  try {
    const { fileContent, filename } = await parseMultipart(event);
    console.log(`Processing file: ${filename}`);

    const settlements = SettlementParser.parseCsv(fileContent);
    console.log(`Parsed ${settlements.length} settlement records`);

    await repository.saveAll(settlements);

    return {
      statusCode: 200,
      headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*", // CORS for HTTP API
      },
      body: JSON.stringify({
        message: "Successfully processed settlement report",
        filename,
        count: settlements.length,
      }),
    };
  } catch (error) {
    console.error("Error processing settlement report:", error);
    return {
      statusCode: 500,
      headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
      },
      body: JSON.stringify({
        message: "Internal server error",
        error: error instanceof Error ? error.message : String(error),
        // Include stack trace only in development/debug mode if needed
        stack:
          process.env.DEBUG === "true" && error instanceof Error
            ? error.stack
            : undefined,
      }),
    };
  }
};
