describe('Smart Filters', function () {

    var humanize, camelCase, fpMethodName, commaSeparated;

    beforeEach(module("smartRegistry.filters"));
    beforeEach(inject(function (humanizeFilter, camelCaseFilter, fpMethodNameFilter, commaSeparatedFilter) {
        humanize = humanizeFilter;
        camelCase = camelCaseFilter;
        fpMethodName = fpMethodNameFilter;
        commaSeparated = commaSeparatedFilter;
    }));

    describe("Humanize", function(){
        it("should return blank if input is null", function(){
            expect(humanize(null)).toEqual("");
        });

        it("should return blank if input is undefined", function(){
            expect(humanize(undefined)).toEqual("");
        });

        it("should return blank if input is blank", function(){
            expect(humanize("")).toEqual("");
        });

        it("should replace underscores with spaces", function(){
            expect(humanize("Female_sterilization_method")).toEqual("Female sterilization method");
        });

        it("should capitalize the first letter", function(){
            expect(humanize("female_sterilization_method")).toEqual("Female sterilization method");
        });

        it("should capitalize the first letter when the char length is 1", function(){
            expect(humanize("f")).toEqual("F");
        });
    });

    describe("Camel Case", function(){
        it("should return blank if input is null", function(){
            expect(camelCase(null)).toEqual("");
        });

        it("should return blank if input is undefined", function(){
            expect(camelCase(undefined)).toEqual("");
        });

        it("should return blank if input is blank", function(){
            expect(camelCase("")).toEqual("");
        });

        it("should convert phrases to camel case", function(){
            expect(camelCase("female sterilization method")).toEqual("Female Sterilization Method");
        });
    });

    describe("FP Method Filter", function(){
        var options = [
            {
                label: "DMPA/Injectable",
                id: "dmpa_injectable",
                handler: "filterByFPMethod"
            }
        ];
        it("should return the input if input is not a known fp method", function(){
            expect(fpMethodName("an_invalid_method", options)).toEqual("an_invalid_method");
        });

        it("should convert iud into IUD", function(){
            expect(fpMethodName("dmpa_injectable", options)).toEqual('DMPA/Injectable');
        });
    });

    describe("Comma Separated", function(){
        it("should return blank if input is null", function(){
            expect(commaSeparated(null)).toEqual("");
        });

        it("should return blank if input is undefined", function(){
            expect(commaSeparated(undefined)).toEqual("");
        });

        it("should return blank if input is blank", function(){
            expect(commaSeparated("")).toEqual("");
        });

        it("should comma separate space separated inputs", function(){
            expect(commaSeparated("pain fever others")).toEqual("pain, fever, others");
        });
    });
});