class Student {
    String name;
    double score;
    String grade;
    boolean pass = true;

    Student(String name, double score) {
        this.name = name;
        this.score = score;

        // Assign grade based on score
        if (score > 95) {
            grade = "A+";
        } else if (score > 90) {
            grade = "A";
        } else if (score > 80) {
            grade = "B";
        } else if (score > 60) {
            grade = "C";
        } else if (score > 45) {
            grade = "D";
        } else {
            grade = "F";
            pass = false;
        }
    }
}
