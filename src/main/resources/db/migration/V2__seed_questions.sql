-- Seed data: 20 sample math questions for Grades 6-10

-- Grade 6 Questions
INSERT INTO questions (subject, topic, grade_level, question_text, correct_answer, solution_steps, difficulty) VALUES
('MATH', 'Fractions', 6, 'Simplify the fraction: 12/16', '3/4', '["Step 1: Find the GCD of 12 and 16, which is 4", "Step 2: Divide both numerator and denominator by 4", "Step 3: 12 ÷ 4 = 3, 16 ÷ 4 = 4", "Final Answer: 3/4"]'::jsonb, 'EASY'),
('MATH', 'Fractions', 6, 'Add the fractions: 1/4 + 2/4', '3/4', '["Step 1: Both fractions have the same denominator (4)", "Step 2: Add the numerators: 1 + 2 = 3", "Step 3: Keep the denominator: 4", "Final Answer: 3/4"]'::jsonb, 'EASY'),
('MATH', 'Decimals', 6, 'Multiply: 2.5 × 4', '10', '["Step 1: Multiply as whole numbers: 25 × 4 = 100", "Step 2: Count decimal places: 2.5 has 1 decimal place", "Step 3: Place decimal in result: 10.0", "Final Answer: 10"]'::jsonb, 'EASY'),
('MATH', 'Percentages', 6, 'What is 25% of 80?', '20', '["Step 1: Convert 25% to decimal: 0.25", "Step 2: Multiply: 80 × 0.25", "Step 3: Calculate: 80 × 0.25 = 20", "Final Answer: 20"]'::jsonb, 'MEDIUM');

-- Grade 7 Questions
INSERT INTO questions (subject, topic, grade_level, question_text, correct_answer, solution_steps, difficulty) VALUES
('MATH', 'Algebra', 7, 'Solve for x: 3x + 5 = 20', 'x = 5', '["Step 1: Subtract 5 from both sides: 3x = 15", "Step 2: Divide both sides by 3: x = 5", "Step 3: Check: 3(5) + 5 = 20 ✓", "Final Answer: x = 5"]'::jsonb, 'MEDIUM'),
('MATH', 'Algebra', 7, 'Simplify: 4x + 3x - 2x', '5x', '["Step 1: Combine like terms", "Step 2: Add coefficients: 4 + 3 - 2 = 5", "Step 3: Keep the variable: x", "Final Answer: 5x"]'::jsonb, 'EASY'),
('MATH', 'Ratios', 7, 'If the ratio of boys to girls is 3:2 and there are 15 boys, how many girls are there?', '10', '["Step 1: Set up proportion: 3/2 = 15/x", "Step 2: Cross multiply: 3x = 30", "Step 3: Divide by 3: x = 10", "Final Answer: 10 girls"]'::jsonb, 'MEDIUM'),
('MATH', 'Integers', 7, 'Calculate: -8 + 12 - 5', '-1', '["Step 1: Start with -8", "Step 2: Add 12: -8 + 12 = 4", "Step 3: Subtract 5: 4 - 5 = -1", "Final Answer: -1"]'::jsonb, 'EASY');

-- Grade 8 Questions
INSERT INTO questions (subject, topic, grade_level, question_text, correct_answer, solution_steps, difficulty) VALUES
('MATH', 'Algebra', 8, 'Solve: 2(x - 3) = 10', 'x = 8', '["Step 1: Distribute: 2x - 6 = 10", "Step 2: Add 6 to both sides: 2x = 16", "Step 3: Divide by 2: x = 8", "Final Answer: x = 8"]'::jsonb, 'MEDIUM'),
('MATH', 'Geometry', 8, 'Find the area of a triangle with base 8 cm and height 5 cm', '20 cm²', '["Step 1: Use formula A = (1/2) × base × height", "Step 2: Substitute: A = (1/2) × 8 × 5", "Step 3: Calculate: A = (1/2) × 40 = 20", "Final Answer: 20 cm²"]'::jsonb, 'EASY'),
('MATH', 'Exponents', 8, 'Simplify: 3² × 3³', '3⁵ or 243', '["Step 1: Use exponent rule: aᵐ × aⁿ = aᵐ⁺ⁿ", "Step 2: Add exponents: 2 + 3 = 5", "Step 3: Result: 3⁵", "Step 4: Calculate: 3⁵ = 243", "Final Answer: 3⁵ = 243"]'::jsonb, 'MEDIUM'),
('MATH', 'Linear Equations', 8, 'Find the slope of the line passing through (2, 3) and (6, 11)', 'm = 2', '["Step 1: Use slope formula: m = (y₂ - y₁)/(x₂ - x₁)", "Step 2: Substitute: m = (11 - 3)/(6 - 2)", "Step 3: Simplify: m = 8/4 = 2", "Final Answer: m = 2"]'::jsonb, 'MEDIUM');

-- Grade 9 Questions
INSERT INTO questions (subject, topic, grade_level, question_text, correct_answer, solution_steps, difficulty) VALUES
('MATH', 'Algebra', 9, 'Solve the quadratic equation: x² - 5x + 6 = 0', 'x = 2 or x = 3', '["Step 1: Factor the quadratic: (x - 2)(x - 3) = 0", "Step 2: Set each factor to zero", "Step 3: x - 2 = 0 → x = 2", "Step 4: x - 3 = 0 → x = 3", "Final Answer: x = 2 or x = 3"]'::jsonb, 'HARD'),
('MATH', 'Functions', 9, 'If f(x) = 2x + 3, find f(5)', 'f(5) = 13', '["Step 1: Substitute x = 5 into f(x) = 2x + 3", "Step 2: f(5) = 2(5) + 3", "Step 3: Calculate: f(5) = 10 + 3 = 13", "Final Answer: 13"]'::jsonb, 'MEDIUM'),
('MATH', 'Geometry', 9, 'Find the circumference of a circle with radius 7 cm (use π ≈ 3.14)', 'C ≈ 43.96 cm', '["Step 1: Use formula C = 2πr", "Step 2: Substitute: C = 2 × 3.14 × 7", "Step 3: Calculate: C = 43.96", "Final Answer: 43.96 cm"]'::jsonb, 'MEDIUM'),
('MATH', 'Pythagorean Theorem', 9, 'Find the length of the hypotenuse if the legs of a right triangle are 3 cm and 4 cm', '5 cm', '["Step 1: Use Pythagorean theorem: a² + b² = c²", "Step 2: Substitute: 3² + 4² = c²", "Step 3: Calculate: 9 + 16 = c²", "Step 4: c² = 25, so c = 5", "Final Answer: 5 cm"]'::jsonb, 'MEDIUM');

-- Grade 10 Questions
INSERT INTO questions (subject, topic, grade_level, question_text, correct_answer, solution_steps, difficulty) VALUES
('MATH', 'Algebra', 10, 'Solve the system: x + y = 10, 2x - y = 5', 'x = 5, y = 5', '["Step 1: Add equations to eliminate y: 3x = 15", "Step 2: Solve for x: x = 5", "Step 3: Substitute into first equation: 5 + y = 10", "Step 4: Solve for y: y = 5", "Final Answer: x = 5, y = 5"]'::jsonb, 'HARD'),
('MATH', 'Trigonometry', 10, 'If sin(θ) = 0.5, what is θ in degrees? (0° ≤ θ ≤ 90°)', 'θ = 30°', '["Step 1: Recall that sin(30°) = 0.5", "Step 2: This is a special angle", "Step 3: Verify: sin(30°) = 1/2 = 0.5 ✓", "Final Answer: θ = 30°"]'::jsonb, 'MEDIUM'),
('MATH', 'Polynomials', 10, 'Factor completely: x² - 9', '(x + 3)(x - 3)', '["Step 1: Recognize difference of squares: a² - b²", "Step 2: x² - 9 = x² - 3²", "Step 3: Apply formula: a² - b² = (a + b)(a - b)", "Step 4: Factor: (x + 3)(x - 3)", "Final Answer: (x + 3)(x - 3)"]'::jsonb, 'MEDIUM'),
('MATH', 'Logarithms', 10, 'Solve for x: log₂(x) = 4', 'x = 16', '["Step 1: Rewrite in exponential form: x = 2⁴", "Step 2: Calculate: 2⁴ = 2 × 2 × 2 × 2", "Step 3: Result: x = 16", "Final Answer: x = 16"]'::jsonb, 'HARD');
