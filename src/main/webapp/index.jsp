<!DOCTYPE html>
<html>
<head>
    <title>Story Generator</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            text-align: center;
            padding: 50px;
        }

        h1 {
            color: #333;
        }

        form {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            display: inline-block;
            margin-top: 20px;
        }

        label {
            margin-right: 10px;
            font-weight: bold;
        }

        select {
            padding: 10px;
            border-radius: 4px;
            border: 1px solid #ddd;
            margin-right: 20px;
        }

        input[type="submit"] {
            background-color: #5c6bc0;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #3f51b5;
        }
    </style>
</head>
<body>
    <h1>Story Generator</h1>
    <form action="animalgenerator" method="get">
        <label for="animal">Type an animal:</label>
        <input type="text" id="animal" name="animal" placeholder="e.g., Dragon, Unicorn" style="padding: 10px; border-radius: 4px; border: 1px solid #ddd; margin-right: 20px;">
        <input type="submit" value="Generate Story">
    </form>
</body>
</html>