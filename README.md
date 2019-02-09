Stepper
====

Stepper is a framework to create AWS Step Function state machines using a programmatic language 
(the Stepper language) instead of JSON. It allows you to author Step Functions using familiar programming constructs
such as `if then else `, `for` and `while` loops and natural expressions `if (arr.length > 1)` 
and generates the appropriate ASL state machine JSON along with a supporting lambda that handles 
complex expressions. You can use any Javascript compliant expression on your data elements.

Here is an example. Given the following Stepper language:

```Javascript
state MyState {

	array1 = ["banana", "apples", "oranges"]

	if (array1.length >= 5) {
		middle = array1[array1.length / 2].toUpperCase();

		task {
			"Parameters": {
        		"MessageBody.$": "$.middle",
        		"QueueUrl": "https://sqs.us-east-1.amazonaws.com/157023xxxxxx/stepqueue1"
      		},
			"Resource": "arn:aws:states:::sqs:sendMessage"
		}
	} else {
		middle = array1[0].toUpperCase();

		task {
			"Parameters": {
        		"MessageBody.$": "$.middle",
        		"QueueUrl": "https://sqs.us-east-1.amazonaws.com/157023xxxxxx/stepqueue2"
      		},
			"Resource": "arn:aws:states:::sqs:sendMessage"
		}
	}

}


```    

the stepper framework generates the following State Machine ASL JSON:

```json
{
    "StartAt": "state000",
    "States": {
            "state000": {
              "Type": "Pass",
              "Result": [
                "banana",
                "apples",
                "oranges"
              ],
              "ResultPath": "$.array1",
              "Next": "state001"
            }
            , "state001": {
              "Type": "Task",
              "Parameters": {
                "cmd__sm": "state001",
                "array1.$": "$.array1"
              },
              "Resource": "arn:aws:lambda:us-east-1:157023xxxxxx:function:testInput",
              "ResultPath": "$.var__000",
              "Next": "state002"
            }
            , "state002": {
              "Type": "Choice",
              "Choices": [
                {
                  "Variable": "$.var__000",
                  "BooleanEquals": true,
                  "Next": "state003"
                },
                {
                  "Variable": "$.var__000",
                  "BooleanEquals": false,
                  "Next": "state005"
                }
              ]
            }
            , "state003": {
              "Type": "Task",
              "ResultPath": "$.middle",
              "Parameters": {
                "cmd__sm": "state003",
                "array1.$": "$.array1"
              },
              "Resource": "arn:aws:lambda:us-east-1:157023xxxxxx:function:testInput",
              "Next": "state004"
            }
            , "state004": {
              "Type": "Task",
              "Parameters": {
                "MessageBody.$": "$.middle",
                "QueueUrl": "https://sqs.us-east-1.amazonaws.com/157023xxxxxx/stepqueue1"
              },
              "Resource": "arn:aws:states:::sqs:sendMessage",
              "Next": "state007"
            }
            , "state005": {
              "Type": "Task",
              "ResultPath": "$.middle",
              "Parameters": {
                "cmd__sm": "state005",
                "array1.$": "$.array1"
              },
              "Resource": "arn:aws:lambda:us-east-1:157023xxxxxx:function:testInput",
              "Next": "state006"
            }
            , "state006": {
              "Type": "Task",
              "Parameters": {
                "MessageBody.$": "$.middle",
                "QueueUrl": "https://sqs.us-east-1.amazonaws.com/157023xxxxxx/stepqueue2"
              },
              "Resource": "arn:aws:states:::sqs:sendMessage",
              "Next": "state007"
            }
            , "state007": {
              "Type": "Succeed"
            }

    }
}
```

It also produces the following Lambda helper function (referened as `testInput` above).

```Javascript

exports.handler = async (event) => {

        if (event.cmd__sm == "state001") {

            var array1 = event.array1;

            const response = array1.length>=5;
            return response;
        }


            if (event.cmd__sm == "state003") {

                var array1 = event.array1;

                const response = array1[array1.length/2].toUpperCase();
                return response;
            }


                if (event.cmd__sm == "state005") {

                    var array1 = event.array1;

                    const response = array1[0].toUpperCase();
                    return response;
                }


    else return {"error": "no branch matched"};
};


```
The code is at `0.1-SNAPSHOT` version and currently only supports `tasks`, `if` statement and assignment expressions. 
The plan is to add the following features for the 1.0 release:

* control structures
    - `while`, `for` control statements
    - iterator for collections
    - `switch` with patttern matching
* `task` state name control
* `wait` and other state types.
* `parallel` task execution 

It would be great to see Stepper grow to a point where it is supported natively in State Functions. 
This will allow even more advanced constructs (dynamic number of parallel tasks for example).

Getting Started
---
     
In the very crude 0.1 version, clone and build the code using gradle. Run `com.eclecticlogic.stepper.Stepper` as your 
main Java class and pass in a reference to the file containing your stepper language. The ASL and supporting 
json will be printed to console.

Next Steps
---

* Unit tests for stepper language grammar
* Release to maven repo
* Create executable jar
* Add support for language constructs to achieve 1.0 release. 