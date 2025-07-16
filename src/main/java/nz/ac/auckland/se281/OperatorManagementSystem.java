package nz.ac.auckland.se281;

import java.util.ArrayList;
import nz.ac.auckland.se281.Types.Location;

public class OperatorManagementSystem {

  // Do not change the parameters of the constructor
  public OperatorManagementSystem() {}

  // ArrayLists to store operator information
  // Each ArrayList corresponds to a specific attribute of the operator
  private ArrayList<String> previousLocations = new ArrayList<>();
  private ArrayList<String> temporaryLocations = new ArrayList<>();
  private ArrayList<String> operatorIdList = new ArrayList<>();
  private ArrayList<String> operatorNameList = new ArrayList<>();
  private ArrayList<String> operatorLocationList = new ArrayList<>();
  private ArrayList<String> operatorLocationAbbreviations = new ArrayList<>();

  // Searches for operators based on a keyword
  // If the keyword is "*", it displays all operators
  // If the keyword is not found, it prints a message saying there are no operators
  // If the keyword is found, it prints the operators that match the keyword
  public void searchOperators(String keyword) {
    keyword = keyword.trim(); // Remove leading and trailing spaces

    // if the arraylist is empty, print that there are no operators found
    if (previousLocations.isEmpty()) {
      MessageCli.OPERATORS_FOUND.printMessage("are", "no", "s", ".");
    }

    if (keyword.equals("*")) {
      String numberLocations = Integer.toString(previousLocations.size());
      if (numberLocations.equalsIgnoreCase("1")) {
        MessageCli.OPERATORS_FOUND.printMessage(
            "is", numberLocations, "", ": * " + previousLocations.get(0));
      } else {

        for (String current : previousLocations) {
          temporaryLocations.add("* " + current);
        }
        String locationsOutput =
            temporaryLocations.toString().replace("[", "").replace("]", "").replace(",", "");
        MessageCli.OPERATORS_FOUND.printMessage(
            "are", numberLocations, "s", ": " + locationsOutput);
      }
    } else if (!keyword
        .codePoints()
        .allMatch(Character::isLetterOrDigit)) { // Check if keyword is alphanumeric
      MessageCli.OPERATORS_FOUND.printMessage("are", "no", "s", ".");
    } else {
      int count = 0;
      for (String place : previousLocations) {
        if (place.toUpperCase().contains(keyword.toUpperCase())) {
          count++;
          temporaryLocations.add("* " + place);
        }
      }
      String outputCount = Integer.toString(count);
      String locationsOutput =
          temporaryLocations.toString().replace("[", "").replace("]", "").replace(",", "");

      // If no operators are found, print a message
      if (outputCount.equals("0")) {
        MessageCli.OPERATORS_FOUND.printMessage("are", "no", "s", ".");
        return;
      }

      // If only one operator is found, print a message
      if (outputCount.equals("1")) {
        MessageCli.OPERATORS_FOUND.printMessage("is", outputCount, "", ": " + locationsOutput);
        return;
      }

      // Must be more than one operator found so print a message
      MessageCli.OPERATORS_FOUND.printMessage("are", outputCount, "s", ": " + locationsOutput);
    }
  }

  // ArrayLists to store activity information
  private ArrayList<String> previousLocationAbbreviations = new ArrayList<>();

  // Creates an operator with the given name and location
  // If the operator name is less than or equal to 3 characters, it prints an error message
  // If the location is not valid, it prints an error message
  // If the operator already exists in the same location, it prints an error message
  // If the operator is created successfully, it prints a success message
  // The operator ID is generated based on the operator name, location, and count of operators in
  // that location
  public void createOperator(String operatorName, String location) {
    // Remove leading and trailing spaces
    if (operatorName.length() <= 3) {
      MessageCli.OPERATOR_NOT_CREATED_INVALID_OPERATOR_NAME.printMessage(operatorName);
      return;
    }

    // Check if the location is valid
    if (Location.fromString(location) == null) {
      MessageCli.OPERATOR_NOT_CREATED_INVALID_LOCATION.printMessage(location);
      return;
    }

    Location locationFound = Location.fromString(location);
    String locationAsString = locationFound.getFullName();
    String locationAbbrev = locationFound.getLocationAbbreviation();

    // Obtain the first letter of each word in the operator name
    // and concatenate them to form the initials
    String[] name = operatorName.split(" ");
    String initials = "";
    for (String letter : name) {
      initials += letter.charAt(0);
    }

    previousLocationAbbreviations.add(locationAbbrev);
    int count = 0;
    for (String i : previousLocationAbbreviations) {
      if (i.equals(locationAbbrev)) {
        count++;
      }
    }

    if (count > 999) {
      count = 999;
    }

    String locationCount = String.format("%03d", count);
    String operatorId = initials + "-" + locationAbbrev + "-" + locationCount;

    // Check if the operator already exists in the same location
    for (String place : previousLocations) {
      if (place.contains(operatorName) && place.contains(locationAsString)) {
        MessageCli.OPERATOR_NOT_CREATED_ALREADY_EXISTS_SAME_LOCATION.printMessage(
            operatorName, locationAsString);
        return;
      }
    }

    // Create the operator and add it to the list
    MessageCli.OPERATOR_CREATED.printMessage(operatorName, operatorId, locationAsString);
    operatorIdList.add(operatorId);
    operatorNameList.add(operatorName);
    operatorLocationList.add(locationAsString);
    operatorLocationAbbreviations.add(locationAbbrev);

    previousLocations.add(
        operatorName + " ('" + operatorId + "' " + "located in '" + locationAsString + "')");
  }

  private ArrayList<String> activityOperatorIds = new ArrayList<>();
  private ArrayList<String> activityUniqueIds = new ArrayList<>();
  private ArrayList<String> activityNameList = new ArrayList<>();
  private ArrayList<String> activityTypeList = new ArrayList<>();
  private ArrayList<Integer> activityMatchIndexes = new ArrayList<>();
  private ArrayList<String> activityOperatorNames = new ArrayList<>();
  private ArrayList<String> activityOperatorLocations = new ArrayList<>();
  private ArrayList<String> activityOperatorLocationAbbreviations = new ArrayList<>();

  // Displays activities for a specific operator ID
  // If the operator ID is not found, it prints an error message
  // If the operator has no activities, it prints a message saying there are no activities
  // If the operator has activities, it prints the activities
  // The activities are displayed in the format: name, ID, type, operator name
  public void viewActivities(String operatorId) {
    activityMatchIndexes.clear(); // Clear the list of matched indexes
    operatorId = operatorId.trim(); // Remove leading and trailing spaces

    // Check if the operator ID is valid and operator has activities
    if (!operatorIdList.contains(operatorId)) {
      MessageCli.OPERATOR_NOT_FOUND.printMessage(operatorId);
    } else if (activityOperatorIds.isEmpty()) {
      MessageCli.ACTIVITIES_FOUND.printMessage("are", "no", "ies", ".");
    } else {
      // If the operator ID is valid and has activities
      // Count the number of activities for the operator ID
      int count = 0;
      for (int i = 0; i < activityOperatorIds.size(); i++) {
        if (activityOperatorIds.get(i).equals(operatorId)) {
          count++;
          activityMatchIndexes.add(i);
        }
      }

      String numberActivities = Integer.toString(count);

      // If 1 activity is found, print a found message and the details.
      if (numberActivities.equals("1")) {
        MessageCli.ACTIVITIES_FOUND.printMessage("is", numberActivities, "y", ": ");
        MessageCli.ACTIVITY_ENTRY.printMessage(
            activityNameList.get(activityMatchIndexes.get(0)),
            activityUniqueIds.get(activityMatchIndexes.get(0)),
            activityTypeList.get(activityMatchIndexes.get(0)),
            activityOperatorNames.get(activityMatchIndexes.get(0)));
      } else {
        // If more than 1 activity is found, print a found message.
        // Then for each activity, print the details.
        MessageCli.ACTIVITIES_FOUND.printMessage("are", numberActivities, "ies", ": ");
        for (int i : activityMatchIndexes) {
          MessageCli.ACTIVITY_ENTRY.printMessage(
              activityNameList.get(i),
              activityUniqueIds.get(i),
              activityTypeList.get(i),
              activityOperatorNames.get(i));
        }
      }
    }
  }

  // Creates an activity with the given name, type, and operator ID
  // If the activity name is less than or equal to 3 characters, it prints an error message
  // If the activity type is not valid, it defaults to "Other"
  // If the operator ID is not found, it prints an error message
  // If the activity is created successfully, it prints a success message
  // The activity ID is generated based on the operator ID and the number of activities created by
  // that operator
  public void createActivity(String activityName, String activityType, String operatorId) {
    activityName = activityName.trim();

    // Check if the activity name is valid
    if (activityName.length() <= 3) {
      MessageCli.ACTIVITY_NOT_CREATED_INVALID_ACTIVITY_NAME.printMessage(activityName);
      return;
    }

    // Check if the activity type is valid
    if (Types.ActivityType.fromString(activityType) == null) {
      activityType = "Other";
      return;
    }

    activityType = Types.ActivityType.fromString(activityType).getName();

    // Check if the operator ID is valid
    if (!operatorIdList.contains(operatorId)) {
      MessageCli.ACTIVITY_NOT_CREATED_INVALID_OPERATOR_ID.printMessage(operatorId);
      return;
    }

    // If operatorId is valid, increase the count of activities for that operator
    activityOperatorIds.add(operatorId);
    int count = 0;
    for (String i : activityOperatorIds) {
      if (i.equals(operatorId)) {
        count++;
      }
    }

    if (count > 999) {
      count = 999;
    }

    // Generate the activity ID based on the operator ID and the count of activities
    // The activity ID is in the format: operatorId-activityCount
    String activityCount = String.format("%03d", count);
    String activityId = operatorId + "-" + activityCount;
    activityUniqueIds.add(activityId);

    // Find the operator name based on the operator ID
    String operatorName = null;
    for (int index = 0; index < operatorIdList.size(); index++) {
      if (operatorIdList.get(index).equals(operatorId)) {
        operatorName = operatorNameList.get(index);
      }
    }

    // Add the activity details to the lists
    activityOperatorLocations.add(operatorLocationList.get(operatorIdList.indexOf(operatorId)));
    activityOperatorLocationAbbreviations.add(
        operatorLocationAbbreviations.get(operatorIdList.indexOf(operatorId)));
    activityOperatorNames.add(operatorName);
    activityNameList.add(activityName);
    activityTypeList.add(activityType);

    // Print a success message for the activity creation
    MessageCli.ACTIVITY_CREATED.printMessage(activityName, activityId, activityType, operatorName);
  }

  // Searches for activities based on a keyword
  // If the keyword is "*", it displays all activities
  // If the keyword is not found, it prints a message saying there are no activities
  // If the keyword is found, it prints the activities that match the keyword
  public void searchActivities(String keyword) {
    activityMatchIndexes.clear(); // Clear the list of matched indexes
    keyword = keyword.trim(); // Remove leading and trailing spaces

    // Check if activities exist
    if (activityOperatorIds.isEmpty()) {
      MessageCli.ACTIVITIES_FOUND.printMessage("are", "no", "ies", ".");
      return;
    }

    // If the keyword is "*", display all activities
    if (keyword.equals("*")) {
      if (activityOperatorIds.size() != 1) {
        MessageCli.ACTIVITIES_FOUND.printMessage(
            "are", String.valueOf(activityOperatorIds.size()), "ies", ":");
      } else {
        MessageCli.ACTIVITIES_FOUND.printMessage("is", "1", "y", ":");
      }

      for (int i = 0; i < activityOperatorIds.size(); i++) {
        String activityName = activityNameList.get(i);
        String activityId = activityUniqueIds.get(i);
        String activityType = activityTypeList.get(i);
        String operatorName = activityOperatorNames.get(i);
        MessageCli.ACTIVITY_ENTRY.printMessage(
            activityName, activityId, activityType, operatorName);
      }
      return;
    }

    // If the keyword is not "*", search for activities that match the keyword
    for (int i = 0; i < activityOperatorIds.size(); i++) {
      String activityName = activityNameList.get(i);
      String activityId = activityUniqueIds.get(i);
      String activityType = activityTypeList.get(i);
      String locationAbbrev = activityOperatorLocationAbbreviations.get(i);
      String locationName = activityOperatorLocations.get(i);

      // Check if the activity name, ID, type, location abbreviation, or location name contains the
      // keyword
      // If it does, add the index to the list of matched indexes
      if (activityName.toUpperCase().contains(keyword.toUpperCase())
          || activityId.toUpperCase().contains(keyword.toUpperCase())
          || activityType.toUpperCase().contains(keyword.toUpperCase())
          || locationAbbrev.toUpperCase().contains(keyword.toUpperCase())
          || locationName.toUpperCase().contains(keyword.toUpperCase())) {
        activityMatchIndexes.add(i);
      }
    }

    // If no activities are found, print a message
    if (activityMatchIndexes.isEmpty()) {
      MessageCli.ACTIVITIES_FOUND.printMessage("are", "no", "ies", ".");
    } else if (activityMatchIndexes.size() == 1) { // If only one activity is found, print a message
      MessageCli.ACTIVITIES_FOUND.printMessage("is", "1", "y", ":");
      MessageCli.ACTIVITY_ENTRY.printMessage(
          activityNameList.get(activityMatchIndexes.get(0)),
          activityUniqueIds.get(activityMatchIndexes.get(0)),
          activityTypeList.get(activityMatchIndexes.get(0)),
          activityOperatorNames.get(activityMatchIndexes.get(0)));
    } else { // If more than one activity is found, print the number of activities found
      // Then for each activity, print the details.
      MessageCli.ACTIVITIES_FOUND.printMessage(
          "are", String.valueOf(activityMatchIndexes.size()), "ies", ":");
      for (int i : activityMatchIndexes) {
        MessageCli.ACTIVITY_ENTRY.printMessage(
            activityNameList.get(i),
            activityUniqueIds.get(i),
            activityTypeList.get(i),
            activityOperatorNames.get(i));
      }
    }
  }

  private ArrayList<String> reviewedActivities = new ArrayList<>();

  private ArrayList<String> reviewIdList = new ArrayList<>();
  private ArrayList<String> ratingList = new ArrayList<>();
  private ArrayList<String> commentsList = new ArrayList<>();
  private ArrayList<String> nameList = new ArrayList<>();
  private ArrayList<String> emailList = new ArrayList<>();
  private ArrayList<String> typeList = new ArrayList<>();
  private ArrayList<Boolean> anonyminityList = new ArrayList<>();
  private ArrayList<Boolean> endorsedList = new ArrayList<>();
  private ArrayList<Boolean> recommendedList = new ArrayList<>();
  private ArrayList<String> resolvedList = new ArrayList<>();
  private ArrayList<String> imageList = new ArrayList<>();

  // Class to represent a review
  // Contains the activity ID, visibility, type, name, email, rating, comments,
  // anonymity, endorsed, recommended, resolved, and images
  class Review {
    private String activityId;
    private boolean visibility;
    private String type;
    private String name;
    private String email;
    private String rating;
    private String comments;
    private boolean anonymity;
    private boolean endorsed;
    private boolean recommended;
    private String resolved;
    private String images;

    public Review(
        String activityId,
        boolean visibility,
        String type,
        String name,
        String email,
        String rating,
        String comments,
        boolean anonymity,
        boolean endorsed,
        boolean recommended,
        String resolved,
        String images) {
      this.activityId = activityId;
      this.visibility = visibility;
      this.type = type;
      this.name = name;
      this.email = email;
      this.rating = rating;
      this.comments = comments;
      this.anonymity = anonymity;
      this.endorsed = endorsed;
      this.recommended = recommended;
      this.resolved = resolved;
      this.images = images;
    }
  }

  // Adds a review for an activity
  // If the activity is not found, it prints an error message
  // If the review is added successfully, it prints a success message
  // The review is added to the list of reviews for the activity
  private void addReview(
      String activityId,
      String type,
      String name,
      String email,
      String rating,
      String comments,
      boolean anonymity,
      boolean endorsed,
      boolean recommended,
      String resolved,
      boolean isPublic,
      String images) {

    // Check if the activity ID is valid
    if (!activityUniqueIds.contains(activityId)) {
      MessageCli.REVIEW_NOT_ADDED_INVALID_ACTIVITY_ID.printMessage(activityId);
      return;
    }

    // Find number of reviews for the activity
    int count = 1;
    for (String i : reviewedActivities) {
      if (i.equals(activityId)) {
        count++;
      }
    }
    if (count > 999) count = 999;

    // Generate the review ID based on the activity ID and the count of reviews
    // The review ID is in the format: activityId-Rcount
    String reviewId = activityId + "-R" + Integer.toString(count);
    String activityName = activityNameList.get(activityUniqueIds.indexOf(activityId));

    // Add the review details to the lists
    reviewedActivities.add(activityId);
    reviewIdList.add(reviewId);
    ratingList.add(rating);
    commentsList.add(comments);
    nameList.add(name);
    emailList.add(email);
    typeList.add(type);
    anonyminityList.add(anonymity);
    endorsedList.add(endorsed);
    recommendedList.add(recommended);
    resolvedList.add(resolved);
    imageList.add(images);

    // Print a success message for the review addition
    MessageCli.REVIEW_ADDED.printMessage(type, reviewId, activityName);
  }

  // Adds a public review for an activity
  // The review is added as a public review
  // The review is visible to the public
  public void addPublicReview(String activityId, String[] options) {
    // Convert the rating to an integer and check if it is within the valid range (1-5)
    // If it is not, set it to the nearest valid value (1 or 5)
    int rating = Integer.parseInt(options[2].trim());
    if (rating < 1) {
      options[2] = "1";
    } else if (rating > 5) {
      options[2] = "5";
    }

    // Check if the name is "Anonymous" and set it to "Anonymous" if the user wants to be anonymous
    if (options[1].equals("y")) {
      options[0] = "Anonymous";
    }

    addReview(
        activityId,
        "Public",
        options[0].trim(),
        null,
        options[2].trim(),
        options[3].trim(),
        Boolean.parseBoolean(options[1].trim()),
        false,
        false,
        null,
        true,
        null);
  }

  // Adds a private review for an activity
  // The review is added as a private review
  // The review is not visible to the public
  public void addPrivateReview(String activityId, String[] options) {
    // Convert the rating to an integer and check if it is within the valid range (1-5)
    // If it is not, set it to the nearest valid value (1 or 5)
    int rating = Integer.parseInt(options[2].trim());
    if (rating < 1) {
      options[2] = "1";
    } else if (rating > 5) {
      options[2] = "5";
    }

    addReview(
        activityId,
        "Private",
        options[0].trim(),
        options[1].trim(),
        options[2].trim(),
        options[3].trim(),
        false,
        false,
        false,
        options[4].trim(),
        false,
        null);
  }

  // Adds an expert review for an activity
  // The review is added as an expert review
  // The review is not visible to the public
  // The review is recommended or not recommended
  // The review can have images associated with it
  public void addExpertReview(String activityId, String[] options) {
    // Convert the rating to an integer and check if it is within the valid range (1-5)
    // If it is not, set it to the nearest valid value (1 or 5)
    int rating = Integer.parseInt(options[1].trim());
    if (rating < 1) {
      options[1] = "1";
    } else if (rating > 5) {
      options[1] = "5";
    }

    // Check if recommended is "y" and set it to true if the user wants to recommend the activity
    boolean recommended = false;
    if (options[3].trim().equals("y")) {
      recommended = true;
    }

    addReview(
        activityId,
        "Expert",
        options[0].trim(),
        null,
        options[1].trim(),
        options[2].trim(),
        false,
        false,
        recommended,
        null,
        true,
        null);
  }

  // Displays reviews for a specific activity
  // If the activity is not found, it prints an error message
  // If the activity has no reviews, it prints a message saying there are no reviews
  // If the activity has reviews, it prints the reviews
  // Has special handling for "Private" reviews and "Expert" reviews
  // If the review is of type "Private", it prints the response to the review
  // If the review is of type "Expert", it prints the recommended status and images
  public void displayReviews(String activityId) {
    // Check if activities exist
    if (activityUniqueIds.isEmpty()) {
      MessageCli.ACTIVITY_NOT_FOUND.printMessage(activityId);
      return;
    }

    // Check if the activity ID is valid
    if (!activityUniqueIds.contains(activityId)) {
      MessageCli.ACTIVITY_NOT_FOUND.printMessage(activityId);
    }

    String activityName = activityNameList.get(activityUniqueIds.indexOf(activityId));
    // Check if the activity has reviews
    if (!reviewedActivities.contains(activityId)) {
      MessageCli.REVIEWS_FOUND.printMessage("are", "no", "s", activityName);
      return;
    }

    // Find the indexes of the reviews for the activity
    // The indexes are stored in the matchedIndexes list
    ArrayList<Integer> matchedIndexes = new ArrayList<>();
    for (int i = 0; i < reviewedActivities.size(); i++) {
      if (reviewedActivities.get(i).equals(activityId)) {
        matchedIndexes.add(i);
      }
    }

    // If the matchedIndexes list is empty, there are no reviews for the activity
    // Print a message saying there are no reviews
    if (matchedIndexes.isEmpty()) {
      MessageCli.REVIEWS_FOUND.printMessage("are", "no", "s", activityName);
      return;
    }

    // Print the number of reviews found for the activity
    String verb = matchedIndexes.size() == 1 ? "is" : "are";
    String suffix = matchedIndexes.size() == 1 ? "" : "s";
    MessageCli.REVIEWS_FOUND.printMessage(
        verb, String.valueOf(matchedIndexes.size()), suffix, activityName);

    // Print the details of each review
    // The details include the rating, type, review ID, name, and comments
    for (int i : matchedIndexes) {
      MessageCli.REVIEW_ENTRY_HEADER.printMessage(
          ratingList.get(i), "5", typeList.get(i), reviewIdList.get(i), nameList.get(i));
      if (endorsedList.get(i)) { // Check if the review is endorsed, print a message if it is
        MessageCli.REVIEW_ENTRY_ENDORSED.printMessage();
      }
      // Print the review comments
      MessageCli.REVIEW_ENTRY_REVIEW_TEXT.printMessage(commentsList.get(i));

      // If the review is type "Private", print the response to the review
      if ("Private".equals(typeList.get(i))) {
        if (resolvedList.get(i).equals("y")) {
          MessageCli.REVIEW_ENTRY_FOLLOW_UP.printMessage(emailList.get(i));
        } else if (resolvedList.get(i).equals("n")) {
          MessageCli.REVIEW_ENTRY_RESOLVED.printMessage("-");
        } else {
          MessageCli.REVIEW_ENTRY_RESOLVED.printMessage(resolvedList.get(i));
        }
      }

      // If the review is type "Expert", print the recommended status and images
      if ("Expert".equals(typeList.get(i))) {
        if (recommendedList.get(i)) {
          MessageCli.REVIEW_ENTRY_RECOMMENDED.printMessage();
        }
        if (imageList.get(i) != null) {
          MessageCli.REVIEW_ENTRY_IMAGES.printMessage(imageList.get(i));
        }
      }
    }
  }

  // Endorses a review by setting the endorsed status to true
  // Review must be of type "Public"
  // If the review is not found, it prints an error message
  // If the review is not of type "Public", it prints an error message
  public void endorseReview(String reviewId) {
    // Check if the review ID is valid
    if (!reviewIdList.contains(reviewId)) {
      MessageCli.REVIEW_NOT_FOUND.printMessage(reviewId);
      return;
    }

    int index = reviewIdList.indexOf(reviewId);
    String reviewType = typeList.get(index);

    // Check if the review is of type "Public"
    // If it is not, print an error message
    if (!"Public".equals(reviewType)) {
      MessageCli.REVIEW_NOT_ENDORSED.printMessage(reviewId);
      return;
    }

    endorsedList.set(index, true);
    MessageCli.REVIEW_ENDORSED.printMessage(reviewId);
  }

  // Resolves a review by setting the response to the review
  // Review must be type "Private"
  // The response is set to the review
  // If the review is not found, it prints an error message
  // If the review is not of type "Private", it prints an error message
  public void resolveReview(String reviewId, String response) {
    // Check if the review ID is valid
    if (!reviewIdList.contains(reviewId)) {
      MessageCli.REVIEW_NOT_FOUND.printMessage(reviewId);
      return;
    }

    // Set index to the index of the review ID in the reviewIdList
    int index = reviewIdList.indexOf(reviewId);

    // Check if the review is of type "Private"
    // If it is, set the response to the review and print a success message
    if (typeList.get(index).equals("Private")) {
      resolvedList.set(index, response);
      MessageCli.REVIEW_RESOLVED.printMessage(reviewId);
    } else {
      MessageCli.REVIEW_NOT_RESOLVED.printMessage(reviewId);
    }
  }

  // Uploads an image for a review
  // If the review is of type "Expert", the image is added to the review
  // If the review is not of type "Expert", it prints an error message
  // If the review is not found, it prints an error message
  public void uploadReviewImage(String reviewId, String imageName) {
    // Check if the review ID is valid
    if (reviewIdList.contains(reviewId)) {
      // Set index to the index of the review ID in the reviewIdList
      int index = reviewIdList.indexOf(reviewId);

      // Check if the review is of type "Expert"
      // If it is, add the image to the review and print a success message
      // If the review is not of type "Expert", print an error message
      if (typeList.get(index).equals("Expert")) {
        MessageCli.REVIEW_IMAGE_ADDED.printMessage(imageName, reviewId);
        if (imageList.get(index) == null) {
          imageList.set(index, imageName);
        } else {
          imageList.set(index, imageList.get(index) + "," + imageName);
        }
      } else {
        MessageCli.REVIEW_IMAGE_NOT_ADDED_NOT_EXPERT.printMessage(reviewId);
      }
    } else {
      MessageCli.REVIEW_NOT_FOUND.printMessage(reviewId);
    }
  }

  // Displays the top activity for each operator location
  // The top activity is the one with the highest average rating
  public void displayTopActivities() {
    // loop through each operator location
    for (int i = 0; i < operatorLocationList.size(); i++) {
      ArrayList<String> activityScoreId = new ArrayList<>();
      ArrayList<Integer> totalRating = new ArrayList<>();
      ArrayList<Integer> totalCount = new ArrayList<>();
      boolean found = false;

      // loop through each reviewed activity
      for (int j = 0; j < reviewedActivities.size(); j++) {
        String reviewedActivity = reviewedActivities.get(j);

        // Check if the reviewed activity is in the current operator location
        // and if the type is not "Private"
        if (reviewedActivity.contains(operatorLocationAbbreviations.get(i))
            && !"Private".equals(typeList.get(j))) {
          // Get the rating for the reviewed activity
          int rating = Integer.parseInt(ratingList.get(j));

          // Check if the activity is already in the list of activity scores
          // If it is, update the total rating and count
          if (activityScoreId.contains(reviewedActivity)) {
            int index = activityScoreId.indexOf(reviewedActivity);
            totalRating.set(index, totalRating.get(index) + rating);
            totalCount.set(index, totalCount.get(index) + 1);

          } else { // If it is not, add it to the list of activity scores
            activityScoreId.add(reviewedActivity);
            totalRating.add(rating);
            totalCount.add(1);
          }
          // Set the found flag to true
          found = true;
        }
      }

      // If no reviewed activities are found in the current operator location, print a message
      if (!found) {
        MessageCli.NO_REVIEWED_ACTIVITIES.printMessage(operatorLocationList.get(i));
        continue;
      }

      int highestAverageRating = 0;
      String highestActivity = null;

      // Find the activity with the highest average rating
      // Loop through the activity scores and calculate the average rating
      for (int k = 0; k < activityScoreId.size(); k++) {
        int currentRating = totalRating.get(k) / totalCount.get(k);
        if (currentRating > highestAverageRating) {
          highestAverageRating = currentRating;
          highestActivity = activityScoreId.get(k);
        }
      }

      // Print the top activity for the current operator location
      // The top activity is the one with the highest average rating
      if (highestActivity != null) {
        int activityNameIndex = activityUniqueIds.indexOf(highestActivity);
        String activityName = activityNameList.get(activityNameIndex);

        MessageCli.TOP_ACTIVITY.printMessage(
            operatorLocationList.get(i), activityName, String.valueOf(highestAverageRating));
      }
    }
  }
}
