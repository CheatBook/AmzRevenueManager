import { APIGatewayProxyEvent, APIGatewayProxyResult } from "aws-lambda";
import { SettlementRepository } from "../services/SettlementRepository";
import { RevenueSummaryService } from "../services/RevenueSummaryService";

const repository = new SettlementRepository();

export const handler = async (
  event: APIGatewayProxyEvent,
): Promise<APIGatewayProxyResult> => {
  try {
    const settlementId = event.queryStringParameters?.settlementId;

    let settlements;
    if (settlementId) {
      console.log(`Fetching revenue summary for settlementId: ${settlementId}`);
      settlements = await repository.findBySettlementId(settlementId);
    } else {
      console.log("Fetching revenue summary for all settlements");
      settlements = await repository.findAll();
    }

    const summaries =
      RevenueSummaryService.calculateSkuRevenueSummaries(settlements);

    return {
      statusCode: 200,
      headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
      },
      body: JSON.stringify(summaries),
    };
  } catch (error) {
    console.error("Error fetching revenue summary:", error);
    return {
      statusCode: 500,
      headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
      },
      body: JSON.stringify({
        message: "Internal server error",
        error: error instanceof Error ? error.message : String(error),
      }),
    };
  }
};
