package Steps.impl;

import DataDefinition.DataDefinitionRegistry;
import DataDefinition.api.IO_NAMES;
import DataDefinition.impl.relation.RelationData;
import Steps.api.AbstractStepDefinition;
import Steps.api.DataDefinitionDeclarationImpl;
import Steps.api.DataNecessity;
import Steps.api.StepResult;
import flow.execution.context.StepExecutionContext;
import java.util.List;

public class CSVExporter extends AbstractStepDefinition {

    public CSVExporter() {
        super("CSV Exporter", true);

        // step inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        // step outputs
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
       RelationData source = context.getDataValue(IO_NAMES.SOURCE, RelationData.class);
        StringBuilder csvBuilder = new StringBuilder();

        int totalLines = source.numOfRows() + 1; // Include header row?
        System.out.println("About to process " + totalLines + " lines of data before starting to work on the table");

        // Write column names to CSV
        List<String> columns = source.getColumns();
        csvBuilder.append(String.join(",", columns));
        csvBuilder.append("\n");

        if (source.isEmpty()) {
            System.out.println("Warning: Source data is empty");
            String summaryLine = "The table is empty of content, so we converted only the column names of the table to the CSV format file.";
            context.storeDataValue("RESULT", csvBuilder);
            return StepResult.WARNING;
        }

            // Write row data to CSV
        for (int rowId = 0; rowId < source.numOfRows(); rowId++) {
            List<String> rowData = source.getRowDataByColumnsOrder(rowId);
            if (!rowData.isEmpty()) {
                csvBuilder.append(String.join(",", rowData));
                csvBuilder.append("\n");
            }
        }

        context.storeDataValue("RESULT", csvBuilder);
        return StepResult.SUCCESS;
    }
}