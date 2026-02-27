import { useEffect } from "react";
import { useForm } from "react-hook-form";
import RegisterProgress from "./RegisterProgress";

import { useRegistrationStore } from "../store/registrationStore";

export interface StudentFormData {
  name: string;
  firstName: string;
  email: string;
  sector: string;
  languages: string[];
  department: string;
  githubLink?: string;
  linkedinLink?: string;
}

type Props = {
  onPrev: () => void;
  onFinish: (data: StudentFormData) => void;
};

const RegisterStep3Student = ({ onPrev, onFinish }: Props) => {
  const { formData, setStep } = useRegistrationStore();
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isValid },
  } = useForm<StudentFormData>({
    mode: "onChange",
    defaultValues: {
      name: formData.name || "",
      firstName: formData.firstName || "",
      email: formData.email || "",
      sector: formData.sector || "",
      languages: formData.languages || [],
      department: formData.department || "",
      githubLink: formData.githubLink || "",
      linkedinLink: formData.linkedinLink || "",
    },
  });

  // S'assurer que l'email est bien enregistré dans RHF (champ readOnly)
  useEffect(() => {
    if (formData.email) {
      setValue("email", formData.email, { shouldValidate: true });
    }
  }, [formData.email, setValue]);

  const onSubmit = (data: StudentFormData) => {
    onFinish(data);
  };

  // Plus besoin de handlePrev, on utilise onPrev directement du parent

  return (
    <div>
      <form className="w-full flex flex-col" onSubmit={handleSubmit(onSubmit)}>
        <label htmlFor="name" className="text-[#e2e2e2] mb-1">
          Nom
        </label>
        <input
          id="name"
          type="text"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register("name", { required: "Le nom est requis." })}
        />
        {errors.name && (
          <span className="text-xs text-red-600 mb-2">
            {errors.name.message}
          </span>
        )}

        <label htmlFor="firstName" className="text-[#e2e2e2] mb-1">
          Prénom
        </label>
        <input
          id="firstName"
          type="text"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register("firstName", { required: "Le prénom est requis." })}
        />
        {errors.firstName && (
          <span className="text-xs text-red-600 mb-2">
            {errors.firstName.message}
          </span>
        )}

        <label htmlFor="email" className="text-[#e2e2e2] mb-1">
          Email
        </label>
        <input
          id="email"
          type="email"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none opacity-70 cursor-not-allowed"
          {...register("email", { required: "L'email est requis." })}
          readOnly
        />
        {errors.email && (
          <span className="text-xs text-red-600 mb-2">
            {errors.email.message}
          </span>
        )}

        <label htmlFor="sector" className="text-[#e2e2e2] mb-1">
          Secteur
        </label>
        <input
          id="sector"
          type="text"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register("sector", { required: "Le secteur est requis." })}
        />
        {errors.sector && (
          <span className="text-xs text-red-600 mb-2">
            {errors.sector.message}
          </span>
        )}

        <label htmlFor="department" className="text-[#e2e2e2] mb-1">
          Département
        </label>
        <select
          id="department"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded outline-none"
          {...register("department", {
            required: "Le département est requis.",
          })}
        >
          <option value="">-- Sélectionnez un département --</option>
          <option value="Informatique">Informatique</option>
          <option value="Génie mécanique">Génie mécanique</option>
          <option value="Administration des affaires">
            Administration des affaires
          </option>
          <option value="Psychologie">Psychologie</option>
          <option value="Biologie">Biologie</option>
          <option value="Droit">Droit</option>
          <option value="Économie">Économie</option>
          <option value="Architecture">Architecture</option>
          <option value="Sciences politiques">Sciences politiques</option>
          <option value="Sciences environnementales">
            Sciences environnementales
          </option>
        </select>
        {errors.department && (
          <span className="text-xs text-red-600 mb-2">
            {errors.department.message}
          </span>
        )}

        <label className="text-[#e2e2e2] mb-1">Langues</label>
        <div className="w-full mb-4 space-y-2 flex flex-col">
          <div className="flex flex-row gap-15">
            <label className="flex items-center text-[#e2e2e2]">
              <input
                type="checkbox"
                value="français"
                className="mr-2 border border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
                {...register("languages", { required: "Au moins une langue." })}
              />
              Français
            </label>
            <label className="flex items-center text-[#e2e2e2]">
              <input
                type="checkbox"
                value="anglais"
                className="mr-2 border border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
                {...register("languages")}
              />
              Anglais
            </label>
            <label className="flex items-center text-[#e2e2e2]">
              <input
                type="checkbox"
                value="autres"
                className="mr-2 border border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
                {...register("languages")}
                onChange={(e) => {
                  // Toggle the "autres" text input visibility
                  const otherInput = document.getElementById(
                    "otherLanguage",
                  ) as HTMLInputElement;
                  if (otherInput) {
                    otherInput.style.display = e.target.checked
                      ? "block"
                      : "none";
                    if (!e.target.checked) {
                      otherInput.value = "";
                    }
                  }
                }}
              />
              Autres
            </label>
          </div>
          <input
            id="otherLanguage"
            type="text"
            placeholder="Précisez la langue"
            className="w-full border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none hidden"
            style={{ display: "none" }}
          />
          <div>
            {errors.languages && (
              <span className="text-xs text-red-600">
                {errors.languages.message}
              </span>
            )}
          </div>
        </div>

        <label htmlFor="githubLink" className="text-[#e2e2e2] mb-1">
          Lien GitHub (optionnel)
        </label>
        <input
          id="githubLink"
          type="url"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register("githubLink")}
        />

        <label htmlFor="linkedinLink" className="text-[#e2e2e2] mb-1">
          Lien LinkedIn (optionnel)
        </label>
        <input
          id="linkedinLink"
          type="url"
          className="w-full mb-4 border text-center border-gray-300 bg-[#e1d3c1] rounded focus:outline-none"
          {...register("linkedinLink")}
        />

        <div className="flex w-full gap-2 justify-between mt-4">
          <button
            type="button"
            className="border border-[var(--color-vert)] hover:bg-gray-300 hover:opacity-80 text-[var(--color-light)] hover:text-gray-800 py-1 px-6 rounded transition-all duration-200 w-full"
            onClick={onPrev}
          >
            Précédent
          </button>
          <button
            type="submit"
            className="bg-[var(--color-vert)] hover:bg-[#63d13e] hover:opacity-90 text-[var(--color-light)] hover:text-white py-1 px-6 rounded transition-all duration-200 disabled:opacity-50 w-full"
            disabled={!isValid}
          >
            Terminer
          </button>
        </div>
      </form>
      <RegisterProgress
        step={3}
        onStepClick={(s) => {
          if (s === 1) setStep(1);
          if (s === 2) setStep(2);
        }}
      />
    </div>
  );
};

export default RegisterStep3Student;
