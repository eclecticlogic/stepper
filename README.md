Stepper
====

**Stepper** is to *Step Functions* as **High level language** is to *Assembly*.
 
Stepper allows you to write AWS Step Functions using modern programming constructs such as `if else` branching, `for` 
and `while` loops, `try catch` for error handling and natural expressions such as `a = arr.length + 1`. 
To illustrate, let us create a step function that generates the first 10 Fibonnaci numbers and stores them into an SQS 
queue.  

```Javascript

@Comment("Generate Fibonnaci numbers")
@TimeoutSeconds(120)
@Version("1.0")
stepper Fibonnaci {
	// first two fibonnaci are static
	
	previous = 1;
	fib = 0;
	
	for (c = 1 to 10) {
		// queue message must be a string
		body = fib + "";
		// write to queue
		sqs = task {
            "Resource": "arn:aws:states:::sqs:sendMessage",
	      	"Parameters": {
	        	"MessageBody.$": "$.body",
	        	"QueueUrl": "https://sqs.us-east-1.amazonaws.com/1570xxxx/fibo"
	      	}
        }
        temp = fib;
		fib += previous;
		previous = temp;
	}
}

```
   
As you can see, the above code feels like a modern programming language (Stepper seeks compatibility with Javascript 
expression syntax and built-in functions). The Stepper compiler turns the variables used into ASL json attributes and 
then creates a supporting Lambda function to evaluate expressions used. Stepper will compile the code above into the 
following state machine.

<details>
    <summary>JSON for Fibonnaci step function ... (click to expand).</summary>
    
```json
{
  "Comment": "Generate Fibonnaci numbers",
  "TimeoutSeconds": 120,
  "Version": "1.0",
  "StartAt": "Fibonnaci000",
  "States": {
    "Fibonnaci000": {
      "Type": "Pass",
      "Result": 1,
      "ResultPath": "$.previous",
      "Next": "Fibonnaci001"
    },
    "Fibonnaci001": {
      "Type": "Pass",
      "Result": 0,
      "ResultPath": "$.fib",
      "Next": "Fibonnaci002"
    },
    "Fibonnaci002": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci002"
      },
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "ResultPath": "$.c",
      "Next": "Fibonnaci003"
    },
    "Fibonnaci003": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci003",
        "c.$": "$.c"
      },
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "ResultPath": "$.Fibonnacivar__000",
      "Next": "Fibonnaci004"
    },
    "Fibonnaci004": {
      "Type": "Choice",
      "Choices": [
        {
          "Variable": "$.Fibonnacivar__000",
          "BooleanEquals": true,
          "Next": "Fibonnaci006"
        },
        {
          "Variable": "$.Fibonnacivar__000",
          "BooleanEquals": false,
          "Next": "Fibonnaci.Success"
        }
      ]
    },
    "Fibonnaci006": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci006",
        "fib.$": "$.fib"
      },
      "ResultPath": "$.body",
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "Next": "Fibonnaci007"
    },
    "Fibonnaci007": {
      "Type": "Task",
      "Resource": "arn:aws:states:::sqs:sendMessage",
      "Parameters": {
        "MessageBody.$": "$.body",
        "QueueUrl": "https://sqs.us-east-1.amazonaws.com/1570xxxx/fibo"
      },
      "ResultPath": "$.sqs",
      "Next": "Fibonnaci008"
    },
    "Fibonnaci008": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci008",
        "fib.$": "$.fib"
      },
      "ResultPath": "$.temp",
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "Next": "Fibonnaci009"
    },
    "Fibonnaci009": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci009",
        "previous.$": "$.previous",
        "fib.$": "$.fib"
      },
      "ResultPath": "$.fib",
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "Next": "Fibonnaci010"
    },
    "Fibonnaci010": {
      "Type": "Task",
      "Next": "Fibonnaci005",
      "Parameters": {
        "cmd__sm": "Fibonnaci010",
        "temp.$": "$.temp"
      },
      "ResultPath": "$.previous",
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda"
    },
    "Fibonnaci005": {
      "Type": "Task",
      "Parameters": {
        "cmd__sm": "Fibonnaci005",
        "c.$": "$.c"
      },
      "Resource": "arn:aws:lambda:us-east-1:1570xxxx:function:Fibonnaci_stepperLambda",
      "ResultPath": "$.c",
      "Next": "Fibonnaci003"
    },
    "Fibonnaci.Success": {
      "Type": "Succeed"
    }
  }
}
```
</details>

<img src="etc/fibonacci.png"/>

Stepper can automatically register the Lambda and create the Step Function or it can output the code for those pieces 
and you can manually register them. Stepper supports:

- [variables, assignments and expressions](../../wiki/Language-Reference#variables)
- [if/else](../../wiki/Language-Reference#branching) branching
- for [loops](../../wiki/Language-Reference#loops) over range with step and looping over collections
- [while loops](../../wiki/Language-Reference#while)
- "[when](../../wiki/Language-Reference#when)" statement, a variation of the traditional switch statement for multi-predicate branching
- [task](../../wiki/Language-Reference#tasks) ASL state for calling activities, accessing queues, etc. 
- [wait and fail](../../wiki/Language-Reference#errors) construct
- annotation driven retry logic
- control over state names
- [parallel](../../wiki/Language-Reference#parallel) state that simply includes other stepper programs to be run concurrently
- [goto](../../wiki/Language-Reference#goto) statement for complex logic
- [try/catch](../../wiki/Language-Reference#trycatch) construct for error handling.

To learn how to get started with Stepper, head over to the [Getting Started](../../wiki/Getting-Started) page of the wiki. The [language reference](../../wiki/Language-Reference) is
also accessible from the wiki pages. 
