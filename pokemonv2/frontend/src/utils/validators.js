const VALID_GENDERS = ['chico', 'chica'];
const MIN_NAME_LENGTH = 1;

export const trainerValidator = {
    validateName(name) {
        if (!name || name.trim().length < MIN_NAME_LENGTH) {
            return { isValid: false, error: 'El nombre no puede estar vacío' };
        }
        if (name.length > 50) {
            return { isValid: false, error: 'El nombre no puede exceder 50 caracteres' };
        }
        return { isValid: true };
    },

    validateGender(gender) {
        if (!gender || !VALID_GENDERS.includes(gender.toLowerCase())) {
            return { isValid: false, error: `El género debe ser uno de: ${VALID_GENDERS.join(', ')}` };
        }
        return { isValid: true };
    },

    validate(trainerData) {
        const errors = {};

        const nameValidation = this.validateName(trainerData.name);
        if (!nameValidation.isValid) {
            errors.name = nameValidation.error;
        }

        const genderValidation = this.validateGender(trainerData.gender);
        if (!genderValidation.isValid) {
            errors.gender = genderValidation.error;
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    }
};
